package me.coley.addressbook.service;

import me.coley.addressbook.exception.DuplicateContactException;
import me.coley.addressbook.exception.MissingContactException;
import me.coley.addressbook.model.Contact;

import java.util.Collection;

/**
 * Contact functionality outline.
 */
public interface ContactService {
	/**
	 * Add a contact.
	 *
	 * @param contact
	 * 		Contact to add.
	 *
	 * @throws DuplicateContactException
	 * 		When a contact with the same identifier as the given one already exists.
	 */
	void add(Contact contact) throws DuplicateContactException;

	/**
	 * Delete an existing contact.
	 *
	 * @param name
	 * 		Name of contact to remove.
	 *
	 * @throws MissingContactException
	 * 		When no contact by the given name could be found.
	 */
	void delete(String name) throws MissingContactException;

	/**
	 * Check if a contact exists by looking up their identifier.
	 *
	 * @param name
	 * 		Name of contact to lookup.
	 *
	 * @return {@code true} when the contact is found. {@code false} otherwise.
	 */
	boolean exists(String name);

	/**
	 * Return a selection of existing contents.
	 *
	 * @param page
	 * 		Page number to pull from. The index is 1-based.
	 * @param length
	 * 		Number of contacts to display per page.
	 * @param query
	 * 		String query.
	 *
	 * @return Contacts on the page.
	 */
	Collection<Contact> contacts(int page, int length, String query);

	/**
	 * Update an existing content by providing a model of the new contact information.
	 *
	 * @param contact
	 * 		A minimal contact that will have its information copied to the existing contact by the
	 * 		same identifier.
	 *
	 * @return Old contact.
	 *
	 * @throws MissingContactException
	 * 		When no matching contact by the given minimal contact's identifier can be found.
	 */
	Contact update(Contact contact) throws MissingContactException;

	/**
	 * Fetch a contact by their unique name.
	 *
	 * @param name
	 * 		Name of contact to fetch.
	 *
	 * @return Contact information.
	 *
	 * @throws MissingContactException
	 * 		When no contact by the given name could be found.
	 */
	Contact get(String name) throws MissingContactException;
}
