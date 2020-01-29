package me.coley.addressbook.endpoint;

import me.coley.addressbook.exception.DuplicateContactException;
import me.coley.addressbook.exception.ElasticException;
import me.coley.addressbook.exception.MissingContactException;
import me.coley.addressbook.model.Contact;
import me.coley.addressbook.model.Phone;
import me.coley.addressbook.util.ResponseWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for {@link Endpoint}
 */
@ExtendWith(MockitoExtension.class)
public class EndpointTests {
	@ParameterizedTest
	@ValueSource(classes = {
			NullPointerException.class,
			MissingContactException.class,
			DuplicateContactException.class,
			IllegalArgumentException.class,
			ElasticException.class
	})
	void testMockThrownExceptions(Class<? extends Exception> exClass) {
		try {
			// Setup mocking for the scenario where the route will throw the given exception
			Request req = mock(Request.class);
			Response resp = mock(Response.class);
			Route route = mock(Route.class);
			when(route.handle(any(), any())).thenThrow(exClass);
			// Call endpoint
			Endpoint end = new Endpoint(route);
			ResponseWrapper wrapper = (ResponseWrapper) end.handle(req, resp);
			assertFalse(wrapper.isSuccess());
			assertNull(wrapper.getData());
			assertEquals(exClass, wrapper.getFailCause().getClass());
		} catch(Exception ex) {
			fail(ex);
		}
	}

	@Test
	void testSuccess() {
		try {
			// Setup mocking for the scenario where the route will throw the given exception
			Request req = mock(Request.class);
			Response resp = mock(Response.class);
			Route route = mock(Route.class);
			Contact retVal = new Contact("Matt", Collections.singletonList(new Phone("333-444-5555", Phone.Type.MOBILE)), "Earth");
			when(route.handle(any(), any())).then(invoke -> ResponseWrapper.success(retVal));
			// Call endpoint
			Endpoint end = new Endpoint(route);
			ResponseWrapper wrapper = (ResponseWrapper) end.handle(req, resp);
			assertTrue(wrapper.isSuccess());
			assertNull(wrapper.getFailCause());
			assertEquals(retVal, wrapper.getData());
		} catch(Exception ex) {
			fail(ex);
		}
	}
}
