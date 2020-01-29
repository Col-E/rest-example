package me.coley.addressbook.model;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Contact information.
 */
public class Contact {
	private final String name;
	private String address;
	private List<Phone> numbers;

	/**
	 * Constructs a contact model with the given name and list of phone numbers.
	 *
	 * @param name
	 * 		Contact's name.
	 * @param numbers
	 * 		Contact's phone numbers.
	 * @param address
	 * 		Contact's home address.
	 *
	 * @throws NullPointerException
	 * 		When the name or number list is {@code null}.
	 */
	public Contact(String name, List<Phone> numbers, String address) throws NullPointerException {
		this.name = requireNonNull(name, "Must specify a contact name");
		this.numbers = requireNonNull(numbers, "Must specify phone numbers list");
		this.address = requireNonNull(address);
	}

	/**
	 * @return Contact's name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Contact's phone numbers.
	 */
	public List<Phone> getNumbers() {
		return numbers;
	}

	/**
	 * @param numbers
	 * 		Contact's phone numbers.
	 */
	public void setNumbers(List<Phone> numbers) {
		this.numbers = numbers;
	}

	/**
	 * @return Contact's home address.
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 * 		Contact's home address.
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		// We have already asserted names are the unique identifiers for contacts
		// so it will be our equality identifier.
		if(other instanceof Contact)
			return name.equals(((Contact) other).name);
		return false;
	}
}
