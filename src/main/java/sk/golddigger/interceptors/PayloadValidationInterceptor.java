package sk.golddigger.interceptors;

import static sk.golddigger.validation.PayloadValidator.validate;
import static sk.golddigger.validation.PayloadValidator.validateSchemaExistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingResponseWrapper;

import sk.golddigger.annotations.SchemaLocation;
import sk.golddigger.exceptions.ApplicationFailure;
import sk.golddigger.exceptions.ClientSideFailure;
import sk.golddigger.http.StreamReusableHttpServletRequest;
import sk.golddigger.validation.PayloadValidator.ValidationResult;

@Component
public class PayloadValidationInterceptor implements HandlerInterceptor {

	// TODO: perform validation only for JSON objects...check content-type! -- or will there only be a json structure?
	// TODO: get rid of duplication
	// TODO: unite these literal messages into message codes
	// TODO: separate the logic into smaller chunks/methods

	private static final Logger logger = Logger.getLogger(PayloadValidationInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
		if (handler instanceof HandlerMethod) {
			SchemaLocation schema = ((HandlerMethod) handler).getMethodAnnotation(SchemaLocation.class);

			// when there is explicitly declared that the schema is not required
			if (schema.noSchema() || schema.inputPath().isEmpty()) {
				return true;
			}

			Resource resource = new DefaultResourceLoader().getResource("classpath:" + schema.inputPath());
			Path inputSchemaLocation = Paths.get(resource.getURI());
			validateSchemaExistence(inputSchemaLocation, "Missing input schema for " + request.getRequestURL());

			String inputSchema = String.join("", Files.readAllLines(inputSchemaLocation, StandardCharsets.UTF_8));
			String payload = new String(((StreamReusableHttpServletRequest)request).getRawData());

			processValidationResult(validate(inputSchema, payload));
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws IOException {
		if (handler instanceof HandlerMethod) {
			ContentCachingResponseWrapper responseWrapper = (ContentCachingResponseWrapper) response;
			SchemaLocation schema = ((HandlerMethod) handler).getMethodAnnotation(SchemaLocation.class);
			if (schema.noSchema() || schema.outputPath().isEmpty()) {
				responseWrapper.copyBodyToResponse(); // I want to know why is this needed by hand
				return;
			}
			Resource resource = new DefaultResourceLoader().getResource("classpath:" + schema.outputPath());
			Path outputSchemaLocation = Paths.get(resource.getURI());
			validateSchemaExistence(outputSchemaLocation, "Missing output schema for " + request.getRequestURL());

			String outputSchema = String.join("", Files.readAllLines(outputSchemaLocation, StandardCharsets.UTF_8));
			String payload = new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
			responseWrapper.copyBodyToResponse();

			processValidationResult(validate(outputSchema, payload));
		}
	}

	private void processValidationResult(ValidationResult validationResult) {
		if (!validationResult.isValid()) {
			logger.error(validationResult.getErrorMessages());
			throw new ApplicationFailure("Validation errors: " + validationResult.getErrorMessages());
		}
	}
}
