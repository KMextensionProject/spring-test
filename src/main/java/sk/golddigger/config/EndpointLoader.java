package sk.golddigger.config;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import sk.golddigger.exceptions.ApplicationFailure;

@Component
public final class EndpointLoader {

	private static final Logger logger = Logger.getLogger(EndpointLoader.class);

	private static final String ENDPOINT_PROTOCOL = "http://";
	private static final String ENDPOINT_PORT = ":8080";
	private static final String ENDPOINT_APP_NAME = "/gold-digger";

	private Set<String> endpoints;
	private InetAddress ipAddress;

	public EndpointLoader() {
		try {
			this.ipAddress = InetAddress.getLocalHost();
			this.endpoints = new HashSet<>(10);
		} catch (UnknownHostException hostError) {
			String errorMessage = resolveMessage("unknownHostError", hostError.getMessage());
			logger.error(errorMessage);
			throw new ApplicationFailure(errorMessage);
		}
	}

	@EventListener
	public void handleContextRefresh(ContextRefreshedEvent event) {
		if (endpoints.isEmpty()) {
			ApplicationContext context = event.getApplicationContext();
			RequestMappingHandlerMapping requestMapping = context.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
			Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMapping.getHandlerMethods();

			handlerMethods.forEach((key, value) -> endpoints.add(constructEndpoint(key.getDirectPaths())));
			logger.info(resolveMessage("endpointsCount", handlerMethods.size()));
		}
	}

	private String constructEndpoint(Set<String> directPaths) {		
		StringBuilder endpoint = new StringBuilder(ENDPOINT_PROTOCOL);
		endpoint.append(ipAddress.getHostAddress());
		endpoint.append(ENDPOINT_PORT);
		endpoint.append(ENDPOINT_APP_NAME);
		endpoint.append(directPaths.toString().substring(1));
		endpoint.deleteCharAt(endpoint.length() - 1);

		return endpoint.toString();
	}

	public Set<String> getApplicationEndpoints() {
		return new HashSet<>(this.endpoints);
	}

}
