package me.coley.addressbook.endpoint;

import me.coley.addressbook.exception.DuplicateContactException;
import me.coley.addressbook.exception.MissingContactException;
import spark.Request;
import spark.Response;
import spark.Route;

import static me.coley.addressbook.util.ResponseWrapper.failure;

/**
 * Route wrapper that adds common response data and error handling.
 */
public class Endpoint implements Route {
	// Error codes for responses to bad requests
	public static final int BAD_REQUEST = 400;
	public static final int NOT_FOUND = 404;
	public static final int CONFLICT = 409;
	public static final int INTERNAL_ERROR = 500;
	// Wrapped route
	private final Route wrapped;

	/**
	 * Constructs an endpoint.
	 *
	 * @param wrapped
	 * 		Route logic to wrap.
	 */
	public Endpoint(Route wrapped) {
		this.wrapped = wrapped;
	}

	/**
	 * Invokes the wrapped route, returning it's response content if no exceptions are thrown.
	 * If an exception is thrown, then the response content outline the appropriate error
	 * response-code.
	 *
	 * @param request
	 * 		The request object providing information about the HTTP request
	 * @param response
	 * 		The response object providing functionality for modifying the response
	 *
	 * @return Response content.
	 */
	@Override
	public Object handle(Request request, Response response) {
		// Set response type, regardless of status this will be the response type.
		response.type("application/json");
		try {
			// Handle wrapped route
			return wrapped.handle(request, response);
		} catch(NullPointerException ex) {
			// Bad request, NPE caused by missing/null parameters in model types and requests
			response.status(BAD_REQUEST);
			return failure(ex);
		} catch(MissingContactException ex) {
			// Bad request, MCE caused when the existing contact cannot be found
			response.status(NOT_FOUND);
			return failure(ex);
		} catch(DuplicateContactException ex) {
			// Bad request, DCE caused by existing contact when none were expected
			response.status(CONFLICT);
			return failure(ex);
		} catch(Exception ex) {
			// Catch for any other issue, such as ElasticException
			response.status(INTERNAL_ERROR);
			return failure(ex);
		}
		// NOTE:
		// Spark has exception handling capabilities, but you cannot control what is returned in
		// the response if you choose to use them. The end-user gets no feedback on what went wrong.
		// So instead we handle it this way, which allows us to specify response codes and messages.
	}
}
