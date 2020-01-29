package me.coley.addressbook.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import me.coley.addressbook.exception.ContactException;
import me.coley.addressbook.model.Contact;
import me.coley.addressbook.model.Phone;

import java.util.ArrayList;
import java.util.List;

/**
 * Json utilities.
 */
public class Json {
	private static final Gson gson;
	static  {
		GsonBuilder builder = new GsonBuilder();
		// Register type for phone
		//  - need to specify using the given constructor so arguments can be validated & normalized
		builder.registerTypeAdapter(Phone.class, (JsonDeserializer<Phone>) (json, type, context) -> {
			JsonObject jsonObject = json.getAsJsonObject();
			String phoneType   = jsonObject.has("type")   ? jsonObject.get("type").getAsString()   : null;
			String phoneNumber = jsonObject.has("number") ? jsonObject.get("number").getAsString() : null;
			return new Phone(phoneNumber, Phone.Type.valueOf(phoneType));
		});
		// Register type for contact
		//  - need to specify using the given constructor so arguments can be validated
		builder.registerTypeAdapter(Contact.class, (JsonDeserializer<Contact>) (json, type, context) -> {
			JsonObject jsonObject = json.getAsJsonObject();
			String contactName = jsonObject.has("name") ? jsonObject.get("name").getAsString() : null;
			String contactAddress = jsonObject.has("address") ? jsonObject.get("address").getAsString() : null;
			List<Phone> contactNumbers = new ArrayList<>();
			if (jsonObject.has("numbers")) {
				jsonObject.get("numbers").getAsJsonArray()
						.forEach(element -> contactNumbers.add(fromJson(element.toString(), Phone.class)));
			}
			return new Contact(contactName, contactNumbers, contactAddress);
		});
		// Register type for contact
		//  - need to specify using the given constructor so arguments can be validated
		builder.registerTypeAdapter(Exception.class, (JsonSerializer<Exception>) (ex, type, context) -> {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("type", ex.getClass().getName());
			jsonObject.addProperty("message", ex.getMessage());
			if (ex instanceof ContactException)
				jsonObject.addProperty("identity", ((ContactException) ex).getName());
			return jsonObject;
		});
		gson = builder.setPrettyPrinting().create();
	}

	/**
	 * Converts the given object to JSON text.
	 *
	 * @param object
	 * 		Object to convert.
	 *
	 * @return Json representation of object.
	 */
	public static String toJson(Object object) {
		return gson.toJson(object);
	}

	/**
	 * Converts the given JSON text to the given type.
	 *
	 * @param json
	 * 		Json respresenting an object.
	 * @param type
	 * 		Class type of object.
	 * @param <T>
	 * 		Generic type of object.
	 *
	 * @return Instance generated from json.
	 */
	public static <T> T fromJson(String json, Class<T> type) {
		return gson.fromJson(json, type);
	}
}
