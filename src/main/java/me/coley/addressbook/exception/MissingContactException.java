package me.coley.addressbook.exception;

/**
 * Exception to manage when looking up a contact fails.
 */
public class MissingContactException extends ContactException {
	/**
	 * Constructs an exception for the attempted reference to a non-existing contact.
	 *
	 * @param name
	 * 		Contact identifier.
	 * @param message
	 * 		Message outlining the exception cause.
	 */
	public MissingContactException(String name, String message) {
		super(name, message);
	}
}
