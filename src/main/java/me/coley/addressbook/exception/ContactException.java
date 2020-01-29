package me.coley.addressbook.exception;

/**
 * Exception to manage when contact service errors.
 */
public abstract class ContactException extends Exception {
	private final String name;

	/**
	 * Construct the common contact error.
	 *
	 * @param name
	 * 		Contact identifier.
	 * @param message
	 * 		Message outlining the exception cause.
	 */
	protected ContactException(String name, String message) {
		super(message);
		this.name = name;
	}

	/**
	 * @return Contact identifier.
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "[" + name + "] : " + getMessage();
	}
}
