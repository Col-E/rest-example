package me.coley.addressbook.service;

import me.coley.addressbook.Options;
import me.coley.addressbook.Server;
import me.coley.addressbook.exception.DuplicateContactException;
import me.coley.addressbook.exception.MissingContactException;
import me.coley.addressbook.model.Contact;
import me.coley.addressbook.model.Phone;
import me.coley.addressbook.service.impl.ElasticContactService;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ElasticContactService}.
 * If an ElasticSearch server is running on the default port, it will be used.
 * Otherwise a bundled server will be downloaded and run.
 */
public class ServiceTests {
	private static final String ADDRESS = "3rd rock from the sun";
	private static final Options LAUNCH_OPTS = new Options();
	private static EmbeddedElastic server;
	private ElasticContactService service;

	@BeforeAll
	static void startServer() {
		// Before any of the tests run, ensure an ElasticSearch server is up.
		//  - None up? Download one and start it.
		try {
			LAUNCH_OPTS.setUseExisting(isElasticSearchRunning());
			server = Server.start(LAUNCH_OPTS);
		} catch(Exception ex) {
			fail("Failed to start server for testing", ex);
		}
	}

	@AfterAll
	static void shutdown() {
		// Stop the ElasticSearch server
		if (server != null)
			server.stop();
	}

	@BeforeEach
	void setup() {
		try {
			// Create a connection to the "test" index and clear it (leftovers from prior tests)
			service = new ElasticContactService("test", new Options());
			service.clear();
		} catch(Exception ex) {
			fail(ex);
		}
	}

	@AfterEach
	void cleanup() {
		try {
			// Close connection to server
			service.close();
		} catch(Exception ex) {
			fail(ex);
		}
	}

	@Test
	void testErrorOnDuplicate() {
		Contact contact = new Contact("Bob", Collections.emptyList(), ADDRESS);
		// First addition succeeds
		assertDoesNotThrow(() -> service.add(contact));
		// Second addition fails
		assertThrows(DuplicateContactException.class, () -> service.add(contact));
	}

	@Test
	void testExists() {
		Contact contact = new Contact("Bob", Collections.emptyList(), ADDRESS);
		// Does not exist
		assertDoesNotThrow(() -> assertFalse(service.exists("Bob")));
		// Add
		assertDoesNotThrow(() -> service.add(contact));
		// Exists now
		assertDoesNotThrow(() -> assertTrue(service.exists("Bob")));
	}

	@Test
	void testDelete() {
		Contact contact = new Contact("Bob", Collections.emptyList(), ADDRESS);
		// Cannot delete what is not there
		assertThrows(MissingContactException.class, () -> service.delete("Bob"));
		// Add
		assertDoesNotThrow(() -> service.add(contact));
		delay(100);
		// Deletion should now succeed
		assertDoesNotThrow(() -> service.delete("Bob"));
	}

	@Test
	void testSearch() {
		// Add 10 dummy contacts
		for(int i = 0; i < 10; i++)
			assertDoesNotThrow(() -> service.add(new Contact(UUID.randomUUID().toString(),
					Collections.emptyList(), ADDRESS)));
		// Add a few non-dummy contacts
		Contact bobby = new Contact("Bobby", Collections.emptyList(), ADDRESS);
		Contact robert = new Contact("Robert", Collections.emptyList(), ADDRESS);
		assertDoesNotThrow(() -> {
			service.add(new Contact("Bob", Collections.emptyList(), ADDRESS));
			service.add(bobby);
			service.add(robert);
		});
		delay(2000);
		// Use search to fetch Robert and Bobby
		Collection<Contact> expected = new HashSet<>(Arrays.asList(bobby, robert));
		Collection<Contact> contacts = new HashSet<>(service.contacts(1, 100, "name:(Robert) OR (Bobby)"));
		assertEquals(expected, contacts);
	}

	@Test
	void testUpdate() {
		Contact contact = new Contact("Bob", Collections.emptyList(), ADDRESS);
		// Cannot delete what is not there
		assertThrows(MissingContactException.class, () -> service.update(contact));
		// Add
		assertDoesNotThrow(() -> service.add(contact));
		// Update should now succeed
		Contact contactUpdated = new Contact("Bob", Collections.singletonList(new Phone("444-333-4444", Phone.Type.MOBILE)), ADDRESS);
		assertDoesNotThrow(() -> {
			// Pop off updated value
			Contact old = service.update(contactUpdated);
			assertTrue(old.getNumbers().isEmpty());
		});
		// Verify value has been set
		assertDoesNotThrow(() -> assertEquals(1, service.get("Bob").getNumbers().size()));
	}

	private static void delay(long time) {
		// Delay a bit so our query isn't too early
		try {
			Thread.sleep(time);
		} catch(InterruptedException ex) { /* ignored */ }
	}

	/**
	 * @return {@code true} if an ElasticSearch server is active on the local machine.
	 */
	private static boolean isElasticSearchRunning() {
		try {
			return new RestHighLevelClient(
					RestClient.builder(
						new HttpHost("localhost", LAUNCH_OPTS.getElasticPort(), "http"))
			).ping(RequestOptions.DEFAULT);
		} catch(Exception ex) {
			return false;
		}
	}
}
