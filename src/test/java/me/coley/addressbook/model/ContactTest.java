package me.coley.addressbook.model;

import me.coley.addressbook.util.Json;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for {@link Contact}
 */
class ContactTest {
	@Test
	void testNullParametersFail() {
		assertThrows(NullPointerException.class, () -> new Contact(null, null, null));
		assertThrows(NullPointerException.class, () -> new Contact(null, Collections.emptyList(), null));
		assertThrows(NullPointerException.class, () -> new Contact("Matt", null, null));
		assertThrows(NullPointerException.class, () -> new Contact("Matt", Collections.emptyList(), null));
		assertThrows(NullPointerException.class, () -> new Contact("Matt", null, "Planet Earth"));
		assertThrows(NullPointerException.class, () -> new Contact(null, Collections.emptyList(), "Planet Earth"));
	}

	@Test
	void testEquality() {
		Contact first = new Contact("Matt", Collections.emptyList(), "Earth");
		Contact second = new Contact("Matt", Collections.emptyList(), "Earth");
		// Equality is based on the name
		assertEquals(first, second);
		assertEquals(first.toString(), second.toString());
		// Hashcode is based on the name
		assertEquals(first.hashCode(), second.hashCode());
	}

	@Test
	void testSerialization() {
		// Object declaration
		List<Phone> numbers = Arrays.asList(
				new Phone("777-222-4444", Phone.Type.HOME),
				new Phone("444-111-2222", Phone.Type.MOBILE));
		Contact base = new Contact("Matt", numbers, "Earth");
		// Json declaration
		//  - pretty-print
		//  - indentation: 2 spaces
		String json = "{\n" +
				"  \"name\": \"Matt\",\n" +
				"  \"address\": \"Earth\",\n" +
				"  \"numbers\": [\n" +
				"    {\n" +
				"      \"number\": \"777-222-4444\",\n" +
				"      \"type\": \"HOME\"\n" +
				"    },\n" +
				"    {\n" +
				"      \"number\": \"444-111-2222\",\n" +
				"      \"type\": \"MOBILE\"\n" +
				"    }\n" +
				"  ]\n" +
				"}";
		Contact converted = Json.fromJson(json, Contact.class);
		// Json ==> Object
		// Object ==> Json
		assertEquals(base, converted);
		assertEquals(json, Json.toJson(base));
		// Check data is equal
		assertEquals("Matt", base.getName());
		assertEquals("Matt", converted.getName());
		assertEquals("Earth", base.getAddress());
		assertEquals("Earth", converted.getAddress());
		assertEquals(numbers, base.getNumbers());
		assertEquals(numbers, converted.getNumbers());
	}
}