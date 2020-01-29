package me.coley.addressbook.service.impl;

import me.coley.addressbook.Options;
import me.coley.addressbook.exception.DuplicateContactException;
import me.coley.addressbook.exception.ElasticException;
import me.coley.addressbook.exception.MissingContactException;
import me.coley.addressbook.model.Contact;
import me.coley.addressbook.service.ContactService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static me.coley.addressbook.util.Json.*;

/**
 * A contact service where the contact information is stored in an ElasticSearch server.
 */
public class ElasticContactService implements ContactService, AutoCloseable {
	private static final Logger LOG = LoggerFactory.getLogger(ElasticContactService.class);
	private final RestHighLevelClient client;
	private final String index;
	private final Options options;

	/**
	 * Constructs an ElasticSearch-backed implementation of {@link ContactService}.
	 *
	 * @param index
	 * 		Target index.
	 * @param options
	 * 		Program arguments
	 */
	public ElasticContactService(String index, Options options) {
		this.index = index;
		this.options = options;
		this.client = createClient();
	}

	@Override
	public void add(Contact contact) throws DuplicateContactException {
		// Verify contact does not already exist
		if(exists(contact.getName()))
			throw new DuplicateContactException(contact.getName(), "Cannot add due to existing contact with same identity");
		try {
			// Create request
			IndexRequest request = new IndexRequest(index);
			request.id(contact.getName());
			request.source(toJson(contact), XContentType.JSON);
			if (options.getElasticVersion().startsWith("6"))
				request.type("contact");
			// Handle response
			IndexResponse response = client.index(request, RequestOptions.DEFAULT);
			if (response.status() == RestStatus.CREATED)
				LOG.info("Added contact: '{}'", contact.getName());
			else
				throw new ElasticException("Failed to index: " + contact.getName() + " - " + response.toString());
		} catch(IOException ex) {
			throw new ElasticException(ex.getMessage());
		}
	}

	@Override
	public void delete(String name) throws MissingContactException {
		// Verify contact already exists
		if(!exists(name))
			throw new MissingContactException(name, "Cannot remove due to no matching contact");
		try {
			// Handle response
			DeleteRequest request = new DeleteRequest(index);
			if (options.getElasticVersion().startsWith("6"))
				request.type("contact");
			request.id(name);
			DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
			if (response.getResult() == DocWriteResponse.Result.DELETED)
				LOG.info("Removed contact: '{}'", name);
			else
				throw new ElasticException("Failed to delete: " + name + " - " + response.toString());
		} catch(IOException ex) {
			throw new ElasticException(ex.getMessage());
		}
	}

	@Override
	public boolean exists(String name) {
		try {
			GetRequest request = new GetRequest(index);
			request.id(name);
			return client.exists(request, RequestOptions.DEFAULT);
		} catch(IOException ex) {
			throw new ElasticException(ex.getMessage());
		}
	}

	@Override
	public Collection<Contact> contacts(int page, int length, String query) {
		// Validate page
		if(page <= 0)
			throw new IllegalStateException("Provided invalid page: " + page);
		int start = (page - 1) * length;
		try {
			// Create request
			SearchRequest request = new SearchRequest(index);
			request.allowPartialSearchResults(true);
			SearchSourceBuilder builder = new SearchSourceBuilder();
			if (query == null)
				builder.query(QueryBuilders.matchAllQuery());
			else
				builder.query(QueryBuilders.queryStringQuery(query));
			builder.from(start);
			builder.size(length);
			request.source(builder);
			// Handle response
			SearchResponse response = client.search(request, RequestOptions.DEFAULT);
			List<Contact> contacts = new ArrayList<>();
			response.getHits().forEach(hit -> contacts.add(fromJson(hit.getSourceAsString(), Contact.class)));
			return contacts;
		} catch(IOException ex) {
			throw new ElasticException(ex.getMessage());
		}
	}

	@Override
	public Contact update(Contact contact) throws MissingContactException {
		// Verify contact already exists
		if(!exists(contact.getName()))
			throw new MissingContactException(contact.getName(), "Cannot update due to no matching contact");
		try {
			Contact old = get(contact.getName());
			UpdateRequest request = new UpdateRequest();
			request.index(index);
			request.id(contact.getName());
			if (options.getElasticVersion().startsWith("6"))
				request.type("contact");
			request.doc(toJson(contact), XContentType.JSON);
			UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
			// Handle response & return updated contact
			if(response.status() == RestStatus.OK) {
				LOG.info("Updated contact: '{}'", contact.getName());
				return old;
			} else
				throw new ElasticException("Failed to update: " + contact.getName() + " - " + response.toString());
		} catch(IOException ex) {
			throw new ElasticException(ex.getMessage());
		}
	}

	@Override
	public Contact get(String name) throws MissingContactException {
		// Verify contact already exists
		if(!exists(name))
			throw new MissingContactException(name, "Cannot retrieve due to no matching contact");
		try {
			// Handle response & return source as type
			GetRequest request = new GetRequest(index);
			request.id(name);
			GetResponse response = client.get(request, RequestOptions.DEFAULT);
			return fromJson(response.getSourceAsString(), Contact.class);
		} catch(IOException ex) {
			throw new ElasticException(ex.getMessage());
		}
	}

	@Override
	public void close() throws IOException {
		client.close();
	}

	/**
	 * Clear all values from the {@link #getIndex() target index}.
	 *
	 * @throws IOException
	 * 		When the server could not verify the current index exists,
	 * 		or when the server fails to delete the current index.
	 */
	public void clear() throws IOException {
		if(client.indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT))
			client.indices().delete(new DeleteIndexRequest(index), RequestOptions.DEFAULT);
	}

	/**
	 * @return Target index.
	 */
	public String getIndex() {
		return index;
	}

	/**
	 * @return Build the ElasticSearch REST client.
	 */
	private RestHighLevelClient createClient() {
		return new RestHighLevelClient(
				RestClient.builder(
						new HttpHost("localhost", options.getElasticPort(), "http"),
						new HttpHost("localhost", options.getElasticPort() + 1, "http")));
	}
}
