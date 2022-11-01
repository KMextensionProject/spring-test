package sk.golddigger.interceptors;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import sk.golddigger.annotations.SchemaLocation;
import sk.golddigger.exceptions.ApplicationFailure;
import sk.golddigger.http.StreamReusableHttpServletRequest;
import sk.golddigger.validation.PayloadValidator;

@Component
public class PayloadValidationInterceptor implements HandlerInterceptor {

	@Autowired
	private PayloadValidator payloadValidator;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
		if (handler instanceof HandlerMethod) {
			SchemaLocation schema = ((HandlerMethod) handler).getMethodAnnotation(SchemaLocation.class);

			// when there is explicitly declared that the schema is not required
			if (schema.noSchema()) {
				return true;
			}

			Resource resource = new DefaultResourceLoader().getResource("classpath:" + schema.inputPath());
			Path inputSchemaLocation = Paths.get(resource.getURI());
			validateSchemaLocationExistence(inputSchemaLocation, "Missing input schema for " + request.getRequestURL());

			String inputSchema = String.join("", Files.readAllLines(inputSchemaLocation, StandardCharsets.UTF_8));
			String payload = new String(((StreamReusableHttpServletRequest)request).getRawData());

			payloadValidator.validate(payload, inputSchema);
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
		
	}

	private void validateSchemaLocationExistence(Path schemaLocation, String errorMessage) {
		if (!Files.exists(schemaLocation)) {
			throw new ApplicationFailure(errorMessage);
		}
	}

//	public static void main(String[] args) {
//		Json.Schema schema = Json.schema(Json.read("{\n"
//				+ "	\"$schema\": \"http://json-schema.org/draft-04/schema#\",\n"
//				+ "	\"type\": \"object\",\n"
//				+ "	\"properties\": {\n"
//				+ "		\"name\": {\n"
//				+ "			\"type\": \"string\"\n"
//				+ "		},\n"
//				+ "		\"priority\": {\n"
//				+ "			\"type\": \"integer\"\n"
//				+ "		},\n"
//				+ "		\"active\": {\n"
//				+ "			\"type\": \"boolean\"\n"
//				+ "		}\n"
//				+ "	},\n"
//				+ "	\"required\": [\n"
//				+ "		\"name\",\n"
//				+ "		\"priority\",\n"
//				+ "		\"active\"\n"
//				+ "	]\n"
//				+ "}"));
//		Json json = schema.validate(Json.read("{\n"
//				+ "	\"name\": \"validation test\",\n"
//				+ "	\"priority\": 1,\n"
//				+ "	\"active\": true\n"
//				+ "}"));
//		System.out.println(json);
//	}
}
