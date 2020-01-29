package me.coley.addressbook.util;

import static me.coley.addressbook.util.Json.toJson;

/**
 * Response wrapper / helper.
 */
public class ResponseWrapper {
	private final boolean success;
	private final Exception failCause;
	private final Object data;

	/**
	 * Constructs a response wrapper containing information about the prior request.
	 *
	 * @param success
	 *        {@code true} if request was a success. {@code false} otherwise.
	 * @param failCause
	 * 		Reason for failure, will be {@code null} if the request was a success.
	 * @param data
	 * 		Response object. May be {@code null} if the request was not a
	 * 		success, or there is no value to return.
	 */
	private ResponseWrapper(boolean success, Exception failCause, Object data) {
		this.success = success;
		this.failCause = failCause;
		this.data = data;
	}

	/**
	 * Create a success response.
	 *
	 * @param data
	 * 		Response object to include.
	 *
	 * @return Success response.
	 */
	public static ResponseWrapper success(Object data) {
		return new ResponseWrapper(true, null, data);
	}

	/**
	 * Create a failure response.
	 *
	 * @param reason
	 * 		Failure reason.
	 *
	 * @return Failure response.
	 */
	public static ResponseWrapper failure(Exception reason) {
		return new ResponseWrapper(false, reason, null);
	}

	/**
	 * @return {@code true} if request was a success. {@code false} otherwise.
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * @return Reason for failure, will be {@code null} if the request was a success.
	 * See {@link #isSuccess()}.
	 */
	public Exception getFailCause() {
		return failCause;
	}

	/**
	 * @return Response object. May be {@code null} if the request was not a success,
	 * or there is no value to return.
	 */
	public Object getData() {
		return data;
	}

	@Override
	public String toString() {
		return toJson(this);
	}
}
