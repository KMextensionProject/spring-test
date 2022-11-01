package sk.golddigger.validation;

import org.springframework.stereotype.Component;

// should have draf4 in its name?
@Component
public class PayloadValidator {

	public void validate(String jsonDraft4Schema, String jsonPayload) {
		Json schema = Json.read(jsonDraft4Schema);
		Json payload = Json.read(jsonPayload);

		Json validationResult = Json.schema(schema).validate(payload);
		// TODO: blow up or return validation results in some meaningful format
		System.out.println("validation result: " + validationResult);
	}

}
