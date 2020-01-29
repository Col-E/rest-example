package me.coley.addressbook.exception;

/**
 * Exception to manage ElasticSearch failures.
 */
public class ElasticException extends RuntimeException {
	/**
	 * Construct an exception to wrap ElasticSearch's error messages and improper return values.
	 *
	 * @param message
	 * 		Message outlining the exception cause.
	 */
	public ElasticException(String message) {
		super(message);
	}
}
