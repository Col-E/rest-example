package me.coley.addressbook.exception;

/**
 * Exception to manage when a contact already exists, preventing addition of a new one.
 */
public class DuplicateContactException extends ContactException {
	/**
	 * Constructs an exception for the attempted duplication of the given contact.
	 *
	 * @param name
	 * 		Contact identifier.
	 * @param message
	 * 		Message outlining the exception cause.
	 */
	public DuplicateContactException(String name, String message) {
		super(name, message);
	}
}
