package sk.golddigger.interceptors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static sk.golddigger.validation.PayloadValidator.validate;
import static sk.golddigger.validation.PayloadValidator.validateSchemaExistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import sk.golddigger.annotations.SchemaLocation;
import sk.golddigger.exceptions.ApplicationFailure;
import sk.golddigger.http.StreamReusableHttpServletRequest;
import sk.golddigger.validation.PayloadValidator.ValidationResult;

@Component
public class PayloadValidationInterceptor implements HandlerInterceptor {


	private static final Logger logger = Logger.getLogger(PayloadValidationInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
		if (handler instanceof HandlerMethod && containsJsonPayload(request)) {
			SchemaLocation schema = ((HandlerMethod) handler).getMethodAnnotation(SchemaLocation.class);

			// when there is explicitly declared that the schema is not required
			if (schema.noSchema() || schema.inputPath().isEmpty()) {
				return true;
			}

			Resource resource = new DefaultResourceLoader().getResource("classpath:" + schema.inputPath());
			Path inputSchemaLocation = Paths.get(resource.getURI());
			validateSchemaExistence(inputSchemaLocation, "Missing input schema for " + request.getRequestURL());

			String inputSchema = String.join("", Files.readAllLines(inputSchemaLocation, UTF_8));
			String payload = new String(((StreamReusableHttpServletRequest)request).getRawData());

			processValidationResult(validate(inputSchema, payload));
		}
		return true;
	}

	private boolean containsJsonPayload(HttpServletRequest request) {
		return "application/json".equals(request.getContentType());
	}

	private void processValidationResult(ValidationResult validationResult) {
		if (!validationResult.isValid()) {
			logger.error(validationResult.getErrorMessages());
			throw new ApplicationFailure("Validation errors: " + validationResult.getErrorMessages());
		}
	}
}
