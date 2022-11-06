package sk.golddigger.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import sk.golddigger.exceptions.ApplicationFailure;

@Component
public class PayloadValidator {

	public static ValidationResult validate(String jsonDraft4Schema, String jsonPayload) {
		Json schema = Json.read(jsonDraft4Schema);
		Json payload = Json.read(jsonPayload);
		Json validationResult = Json.schema(schema).validate(payload);

		return new ValidationResult(validationResult);
	}

	public static class ValidationResult {

		private boolean isValid;
		private List<String> errors;

		public ValidationResult(Json validationResult) {
			this(validationResult.asMap());
		}

		public ValidationResult(Map<String, Object> validationResult) {
			validateResultPresence(validationResult);
			initializeResultProperties(validationResult);
		}

		private void initializeResultProperties(Map<String, Object> validationResultSource) {
			Object ok = validationResultSource.get("ok");
			if (ok instanceof Boolean) { // covers null reference
				this.isValid = (boolean) ok;
			}
			@SuppressWarnings("unchecked")
			List<String> validationErrors = (List<String>) validationResultSource.get("error");
			if (validationErrors != null) {
				this.errors = validationErrors;
			} else {
				this.errors = new ArrayList<>(0);
			}
		}

		private void validateResultPresence(Map<String, Object> validationResultSource) {
			if (validationResultSource == null) {
				throw new ApplicationFailure("validation result cannot be null");
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
