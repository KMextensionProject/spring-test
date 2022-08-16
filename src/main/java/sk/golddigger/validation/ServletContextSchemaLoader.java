package sk.golddigger.validation;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import sk.golddigger.annotations.SchemaLocation;
import sk.golddigger.exceptions.ApplicationFailure;

/**
 * This class is used for locating and retrieving the schema resource from {@link ServletContext} associated with a {@link HandlerMethod}.
 * The schema resources have to be located in <b>/resources/schemas/</b> folder.
 * 
 * @author mhlavac
 * 
 * @see HandlerMethod
 * @see ServletContextResource
 */
@Component
public class ServletContextSchemaLoader {

	@Autowired
	private ServletContext servletContext;

	private static final String SCHEMA_PREFIX = "/resources/schemas/";

	/**
	 * @param schemaPath
	 * @return schema resource from servlet context (from /resources/schemas/ folder located in web application root)
	 */
	public File locateSchema(String schemaPath) {
		Assert.hasLength(schemaPath, "Expecting relative schema path");
		String prefixedSchemaPath = SCHEMA_PREFIX + schemaPath;
		ServletContextResource contextResource = new ServletContextResource(servletContext, prefixedSchemaPath);
		if (!contextResource.exists() || !contextResource.isReadable()) {
			throw new ApplicationFailure("Unable to locate or read schema " + prefixedSchemaPath);
		}
		try {
			return contextResource.getFile();
		} catch (IOException e) {
			throw new ApplicationFailure("Unable to locate or read schema " + prefixedSchemaPath, e);
		}
	}

	/**
	 * returns relative input schema path specified in {@link SchemaLocation} annotation on specified handler; <code>null</code>
	 * if no input path is specified, or {@link SchemaLocation#noSchema()} is true.
	 */
	public String getInSchemaResourcePath(HandlerMethod handler, HttpServletRequest request) {
		SchemaLocation schemaLocation = getSchemaLocation(handler);
		if (schemaLocation == null) {
			return null;
		}
		return getSchemaResourcePath(schemaLocation.inputPath(), request);
	}

	/**
	 * returns relative output schema path specified in {@link SchemaLocation} annotation on specified handler; <code>null</code>
	 * if no output path is specified, or {@link SchemaLocation#noSchema()} is true.
	 */
	public String getOutSchemaResourcePath(HandlerMethod handler, HttpServletRequest request) {
		SchemaLocation schemaLocation = getSchemaLocation(handler);
		if (schemaLocation == null) {
			return null;
		}
		return getSchemaResourcePath(schemaLocation.outputPath(), request);
	}

	/**
	 * returns relative output schema path specified in {@link SchemaLocation} annotation on specified method; <code>null</code>
	 * if no output path is specified, or {@link SchemaLocation#noSchema()} is true. <b>No dynamic parts in schema location are expanded.</b>
	 */
	public String getOutSchemaResourcePath(Method method) {
		SchemaLocation schemaLocation = method.getAnnotation(SchemaLocation.class);
		if (schemaLocation == null) {
			return null;
		}
		return getSchemaResourcePath(schemaLocation.outputPath(), null);
	}

	private SchemaLocation getSchemaLocation(HandlerMethod handler) {
		SchemaLocation schemaLocation = AnnotationUtils.getAnnotation(handler.getMethod(), SchemaLocation.class);

		if (schemaLocation == null) {
			throw new IllegalStateException("Missing Schema location annotation!");
		}

		if (schemaLocation.noSchema()) {
			// no schema == don't validate
			return null;
		}

		return schemaLocation;
	}

	private String getSchemaResourcePath(String outputFile, HttpServletRequest request) {
		if (outputFile.isEmpty()) {
			// no schema == don't validate
			return null;
		}

		String returnedOutputFile = outputFile;
		if (returnedOutputFile.startsWith("/")) {
			returnedOutputFile = returnedOutputFile.substring(1);
		}

		if (request != null) {
			@SuppressWarnings("unchecked")
			Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
			//for dynamic controllers like /enum/{enumName}
			for (Entry<String, String> entry : pathVariables.entrySet()) {
				returnedOutputFile = returnedOutputFile.replaceAll("\\{" + entry.getKey() + "\\}", entry.getValue());
			}
		}

		return returnedOutputFile;
	}
	
	@PostConstruct 
	private void letsSee() {
		System.out.println("schema location handler loaded");
	}
}