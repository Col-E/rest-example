package me.coley.addressbook.model;

import me.coley.addressbook.util.Json;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for {@link Phone}
 */
class PhoneTests {
	@Test
	void testNullParametersFail() {
		assertThrows(NullPointerException.class, () -> new Phone(null, null));
		assertThrows(NullPointerException.class, () -> new Phone(null, Phone.Type.HOME));
		assertThrows(NullPointerException.class, () -> new Phone("999-999-9999", null));
	}

	@Test
	void testEquality() {
		String number = "444-555-6666";
		Phone.Type type = Phone.Type.MOBILE;
		Phone first = new Phone(number, type);
		Phone second = new Phone(number, type);
		// Hashcode/Equality is based on the number & type
		assertEquals(first, second);
		assertEquals(first.toString(), second.toString());
		assertEquals(first.hashCode(), second.hashCode());
	}

	@ParameterizedTest
	@CsvSource({
			// No changes
			"777-888-9999,777-888-9999",
			// Adds '-' split
			"777-888-9999,7778889999"
	})
	void testNormalization(String expected, String given) {
		assertEquals(expected, new Phone(given, Phone.Type.MOBILE).getNumber());
	}

	@ParameterizedTest
	@ValueSource(strings = {
			// bad first digit
			"099-999-9999",
			"199-999-9999",
			// bad size
			"999-999-999",
			"999-99-9999",
			"99-999-9999",
			"9999-999-9999",
			"999-9999-9999",
			"999999999",
			"99999999999",
			// non-numeric
			"999-9A9-9999",
			"A99-999-9999",
			"999A999999"
	})
	void testNumberValidation(String number) {
		assertThrows(IllegalArgumentException.class, () -> new Phone(number, Phone.Type.HOME));
	}

	@Test
	void testSerialization() {
		String number = "444-555-6666";
		Phone.Type type = Phone.Type.MOBILE;
		// Object declaration
		Phone base = new Phone(number, type);
		// Json declaration
		//  - pretty-print
		//  - indentation: 2 spaces
		String json = "{\n" +
				"  \"number\": \"" + number + "\",\n" +
				"  \"type\": \"" + type.name() + "\"\n" +
				"}";
		Phone converted = Json.fromJson(json, Phone.class);
		// Json ==> Object
		// Object ==> Json
		assertEquals(base, converted);
		assertEquals(json, Json.toJson(base));
		// Check data is equal
		assertEquals(base.getType(), converted.getType());
		assertEquals(base.getNumber(), converted.getNumber());
	}
}