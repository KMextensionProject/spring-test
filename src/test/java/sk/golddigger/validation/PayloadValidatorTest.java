package sk.golddigger.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PayloadValidatorTest {

	@Test
	public void validationTest() {
		String schema = "{\n\"$schema\": \"http://json-schema.org/draft-04/schema#\",\n\"type\": \"object\",\n\"properties\": "
				+ "{\n\"name\": {\n\"type\": \"string\"\n},\n\"priority\": {\n\"type\": \"integer\"\n},\n\"active\": {\n\"type\": "
				+ "\"boolean\"\n}\n},\n\"required\": [\n\"name\",\n\"priority\",\n\"active\"\n]\n}";
		String payload = "{\n\"name\": \"validation test\",\n\"priority\": true,\n\"active\": 1\n}";

//		Json js = Json.read(schema);
//		Json jsp = Json.read(payload);
//		System.out.println(jsp.set("active", "true"));
		
		// if it is valid with all the required elements having no additional elements and no not-required elements
		boolean actual = PayloadValidator.validate(schema, payload).isValid();
		Assertions.assertEquals(true, actual, "");

		// if it isn't valid due to missing required field
		actual = PayloadValidator.validate(schema, payload).isValid();
		Assertions.assertEquals(false, actual, "");

		// if it isn't valid due to invalid data type in required field

		// if it isn't valid due to additional field that is not defined --> all fields that come should be in schema (awaited)
	}

	@Test
	public void validationResultTest() {

	}
}
