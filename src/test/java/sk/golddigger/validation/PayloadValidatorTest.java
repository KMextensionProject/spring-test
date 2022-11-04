package sk.golddigger.validation;

import org.junit.jupiter.api.Test;

public class PayloadValidatorTest {

	@Test
	public void validationTest() {
		
	}

	@Test
	public void validationResultTest() {
		
	}

//	public static void main(String[] args) {
//	Json.Schema schema = Json
//			.schema(Json.read("{\n" + "	\"$schema\": \"http://json-schema.org/draft-04/schema#\",\n"
//					+ "	\"type\": \"object\",\n" + "	\"properties\": {\n" + "		\"name\": {\n"
//					+ "			\"type\": \"string\"\n" + "		},\n" + "		\"priority\": {\n"
//					+ "			\"type\": \"integer\"\n" + "		},\n" + "		\"active\": {\n"
//					+ "			\"type\": \"boolean\"\n" + "		}\n" + "	},\n" + "	\"required\": [\n"
//					+ "		\"name\",\n" + "		\"priority\",\n" + "		\"active\"\n" + "	]\n" + "}"));
//	Json json = schema.validate(Json.read("{\n" + "	\"name\": \"validation test\",\n" + "	\"priority\": true,\n"
//			+ "	\"active\": 1\n" + "}"));
////	System.out.println(json);
//
//	boolean isValid = (boolean) json.asMap().get("ok");
//	if (isValid) {
//		System.out.println("document is valid");
//		// return empty list of validation errors
//	} else {
//		System.out.println("document is invalid");
//		// return list of errors as single string messages
//		new ValidationResult(json.asMap());
//	}
//}
	
}
