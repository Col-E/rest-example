package me.coley.addressbook.model;

import static java.util.Objects.*;

/**
 * Phone contact information.
 */
public class Phone {
	private final String number;
	private final Type type;

	/**
	 * Constructs a phone model with a number and number type.
	 *
	 * @param number
	 * 		Contact phone number. Must be in one of the given formats:
	 * 		<ul>
	 * 		<li>{@code NNN-NNN-NNNN}</li>
	 * 		<li>{@code NNNNNNNNNN}</li>
	 * 		</ul>
	 * @param type
	 * 		Kind of phone contact.
	 *
	 * @throws NullPointerException
	 * 		When the number or type is {@code null}.
	 * @throws IllegalArgumentException
	 * 		When the number is not in the required format.
	 */
	public Phone(String number, Type type) throws NullPointerException, IllegalArgumentException {
		this.number = normalize(requireNonNull(number, "Must specify phone number"));
		this.type = requireNonNull(type, "Must specify phone number type");
	}

	/**
	 * @return Phone number in the format: {@code NNN-NNN-NNNN}
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * @return Type of phone contact.
	 */
	public Type getType() {
		return type;
	}

	@Override
	public String toString() {
		return getType().name() + ":" + getNumber();
	}

	@Override
	public int hashCode() {
		return hash(number, type);
	}

	@Override
	public boolean equals(Object other) {
		if(other instanceof Phone) {
			Phone otherPhone = (Phone) other;
			return number.equals(otherPhone.number) && type == otherPhone.type;
		}
		return super.equals(other);
	}

	/**
	 * @param number
	 * 		Potential phone number.
	 *
	 * @return Number formatted in the style {@code NNN-NNN-NNNN}.
	 */
	private static String normalize(String number) {
		if(number.matches("[2-9]\\d{2}-\\d{3}-\\d{4}")) {
			// Number already matches expected format.
			return number;
		}
		if(number.matches("[2-9]\\d{9}")) {
			// Number is valid, but missing '-' splitters.
			return number.substring(0, 3) + "-" + number.substring(3, 6) + "-" + number.substring(6);
		}
		// Number is not in one of the allowed formats.
		throw new IllegalArgumentException("Invalid phone format: " + number);
	}

	/**
	 * Phone contact type.
	 */
	public enum Type {MOBILE, HOME, OTHER}
}
