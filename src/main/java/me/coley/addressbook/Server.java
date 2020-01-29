package me.coley.addressbook;

import me.coley.addressbook.endpoint.Endpoint;
import me.coley.addressbook.model.Contact;
import me.coley.addressbook.service.ContactService;
import me.coley.addressbook.service.impl.ElasticContactService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;
import pl.allegro.tech.embeddedelasticsearch.PopularProperties;

import java.io.File;

import static spark.Spark.*;
import static me.coley.addressbook.util.Json.fromJson;
import static me.coley.addressbook.util.ResponseWrapper.success;

/**
 * Main class for the address book server.
 */
public class Server {
	private static final Logger LOG = LoggerFactory.getLogger(Server.class);
	private static final String INDEX = "book";
	private static final Options options = new Options();

	/**
	 * Read arguments and start the server.
	 *
	 * @param args
	 * 		Program arguments.
	 */
	public static void main(String[] args) {
		try {
			CommandLine cl = new CommandLine(options);
			cl.parseArgs(args);
			if(options.wasHelpRequested()) {
				cl.usage(System.out);
				return;
			}
			start(options);
		} catch(Exception ex) {
			LOG.error("Failed to start ElasticSearch server", ex);
			return;
		}
		setup();
	}

	/**
	 * Starts the server with the given options.
	 *
	 * @param options
	 * 		Options to use for running the server.
	 *
	 * @return ElasticSearch wrapper. Will be {@code null} if the {@link #options} specify
	 * {@link Options#useExisting()}.
	 *
	 * @throws Exception
	 * 		When the ElasticServer instance could not be started.
	 */
	public static EmbeddedElastic start(Options options) throws Exception {
		// Setup directory for ElasticSearch
		File downloadDir = new File("elasticsearch-server");
		if (!downloadDir.exists())
			downloadDir.mkdir();
		// Spin up ElasticSearch
		EmbeddedElastic embeddedElastic = null;
		if (!options.useExisting()) {
			embeddedElastic = EmbeddedElastic.builder()
					.withElasticVersion(options.getElasticVersion())
					.withSetting(PopularProperties.HTTP_PORT, options.getElasticPort())
					.withDownloadDirectory(downloadDir).build();
			embeddedElastic.start();
		}
		// Ignite SparkJava
		port(options.getSparkPort());
		return embeddedElastic;
	}

	/**
	 * Spark server setup.
	 */
	private static void setup() {
		initExceptionHandler((ex) -> LOG.error("Failed to initialize spark server", ex));
		// Create service
		ContactService service = new ElasticContactService(INDEX, options);
		// Add contact
		post("/contact", new Endpoint((req, res)-> {
			Contact contact = fromJson(req.body(), Contact.class);
			service.add(contact);
			return success(contact);
		}));
		// Delete contact
		delete("/contact/:name", new Endpoint((req, res) -> {
			service.delete(req.params(":name"));
			return success(null);
		}));
		// List contacts
		get("/contact", new Endpoint((req, res) -> {
			String pageSizeStr = req.queryParams("pageSize");
			if (pageSizeStr == null || !pageSizeStr.matches("\\d+"))
				throw new NullPointerException("Missing page size parameter");
			String pageStr = req.queryParams("page");
			if (pageStr == null || !pageStr.matches("\\d+"))
				throw new NullPointerException("Missing page parameter");
			String queryStr = req.queryParams("query");
			int pageSize = Integer.parseInt(pageSizeStr);
			int page = Integer.parseInt(pageStr);
			return success(service.contacts(page, pageSize, queryStr));
		}));
		// Update contact
		put("/contact", new Endpoint((req, res) -> {
			Contact contact = fromJson(req.body(), Contact.class);
			Contact old = service.update(contact);
			return success(old);
		}));
		// Fetch contact
		get("/contact/:name", new Endpoint((req, res) -> {
			Contact contact = service.get(req.params(":name"));
			return success(contact);
		}));
	}
}