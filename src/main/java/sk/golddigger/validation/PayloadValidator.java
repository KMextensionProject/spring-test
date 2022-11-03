package sk.golddigger.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

// should have draf4 in its name?
@Component
public class PayloadValidator {

	public ValidationResult validate(String jsonDraft4Schema, String jsonPayload) {
		Json schema = Json.read(jsonDraft4Schema);
		Json payload = Json.read(jsonPayload);
		Json validationResult = Json.schema(schema).validate(payload);
		return new ValidationResult(validationResult);
	}

//	public static void main(String[] args) {
//		Json.Schema schema = Json
//				.schema(Json.read("{\n" + "	\"$schema\": \"http://json-schema.org/draft-04/schema#\",\n"
//						+ "	\"type\": \"object\",\n" + "	\"properties\": {\n" + "		\"name\": {\n"
//						+ "			\"type\": \"string\"\n" + "		},\n" + "		\"priority\": {\n"
//						+ "			\"type\": \"integer\"\n" + "		},\n" + "		\"active\": {\n"
//						+ "			\"type\": \"boolean\"\n" + "		}\n" + "	},\n" + "	\"required\": [\n"
//						+ "		\"name\",\n" + "		\"priority\",\n" + "		\"active\"\n" + "	]\n" + "}"));
//		Json json = schema.validate(Json.read("{\n" + "	\"name\": \"validation test\",\n" + "	\"priority\": true,\n"
//				+ "	\"active\": 1\n" + "}"));
////		System.out.println(json);
//
//		boolean isValid = (boolean) json.asMap().get("ok");
//		if (isValid) {
//			System.out.println("document is valid");
//			// return empty list of validation errors
//		} else {
//			System.out.println("document is invalid");
//			// return list of errors as single string messages
//			new ValidationResult(json.asMap());
//		}
//	}

	public static class ValidationResult {
		private boolean isValid;
		private List<String> errors;

		public ValidationResult(Json validationResult) {
			this(validationResult.asMap());
		}

		public ValidationResult(Map<String, Object> validationResult) {
			Object ok = validationResult.get("ok");
			if (ok instanceof Boolean) {
				this.isValid = (boolean) ok;
			}
			List<String> validationResults = (List<String>) validationResult.get("error");
			if (validationResults != null) {
				this.errors = validationResults;
			} else {
				this.errors = new ArrayList<>(0);
			}
		}

		public boolean isValid() {
			return this.isValid;
		}

		public List<String> getErrorMessages() {
			return new ArrayList<>(this.errors);
		}
	}
}
