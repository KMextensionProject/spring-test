package sk.golddigger.config;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.Query;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import sk.golddigger.annotations.SchemaLocation;
import sk.golddigger.exceptions.ApplicationFailure;

@Component
public final class EndpointLoader {

	private static final Logger logger = Logger.getLogger(EndpointLoader.class);

	private static final String ENDPOINT_PROTOCOL = "http://";
	private static final String ENDPOINT_APP_NAME = "/gold-digger";

	private Set<String> endpoints;
	private String ipAddress;
	private String endpointPort;

	@Autowired
	private ServletContext servletContext;

	public EndpointLoader() {
		this.endpoints = new HashSet<>(10);
	}

	@EventListener
	private void handleContextRefresh(ContextRefreshedEvent event) throws UnknownHostException, MalformedObjectNameException {
		if (endpoints.isEmpty()) {
			ApplicationContext context = event.getApplicationContext();
			RequestMappingHandlerMapping requestMapping = context.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
			Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMapping.getHandlerMethods();

			// validate if all controllers have schema locations on them
			validateControllerSchemaLocationPresence(handlerMethods.values());

			if (ipAddress != null) {
				handlerMethods.forEach((key, value) -> endpoints.add(constructEndpoint(key.getDirectPaths())));
				logger.info(resolveMessage("endpointsCount", handlerMethods.size()));
			}

			logHomePageURL();
		}
	}

	private void validateControllerSchemaLocationPresence(Collection<HandlerMethod> handlerMethods) {		
		boolean errorous = false;
		for (HandlerMethod controller : handlerMethods) {
			if (!controller.hasMethodAnnotation(SchemaLocation.class)) {
				logger.error(controller.getShortLogMessage());
				if (!errorous) {
					errorous = true;
				}
			}
		}
		if (errorous) {
			// there is no point keeping application alive
			System.exit(500);
		}
	}

	private String constructEndpoint(Set<String> directPaths) {		
		StringBuilder endpoint = new StringBuilder(ENDPOINT_PROTOCOL);
		endpoint.append(ipAddress);
		endpoint.append(endpointPort);
		endpoint.append(ENDPOINT_APP_NAME);
		endpoint.append(directPaths.toString().substring(1));
		endpoint.deleteCharAt(endpoint.length() - 1);

		return endpoint.toString();
	}

	public Set<String> getApplicationEndpoints() {
		return new HashSet<>(this.endpoints);
	}

	public String getServerIpAddress() {
		return ipAddress;
	}

	@PostConstruct
	private void initializeServerAddress() {
		// for JSP
		ipAddress = lookupServerIpAddress();
		endpointPort = lookupServerPort();
		servletContext.setAttribute("ip", ipAddress);
		servletContext.setAttribute("port", endpointPort);
	}

	private String lookupServerIpAddress() {
		try (Socket s = new Socket()) {
			s.connect(new InetSocketAddress("www.google.com", 80));
			return s.getLocalAddress().getHostAddress();
		} catch (IOException ioe) {
			String serverIpNotFoundMessage = resolveMessage("serverIpNotFound");
			logger.warn(serverIpNotFoundMessage);
			throw new ApplicationFailure(serverIpNotFoundMessage);
		}
	}

	private String lookupServerPort() {
		MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
		try {
			Set<ObjectName> objectNames = beanServer.queryNames(new ObjectName("*:type=Connector,*"),
				Query.match(Query.attr("protocol"),
				Query.value("HTTP/1.1")));

			return ":".concat(objectNames.iterator().next().getKeyProperty("port"));
		} catch (MalformedObjectNameException | NoSuchElementException e) {
			throw new ApplicationFailure("Could not lookup the server port");
		}
	}

	/*
	 * if the home page is not found, the user won't know 
	 * how to access the application from another device
	 */
	private void logHomePageURL() {

		Optional<String> homeEndpoint = endpoints.stream()
				.filter(e -> e.contains("home"))
				.findFirst();

		if (!homeEndpoint.isPresent()) {
			String messageError = resolveMessage("missingHomeEndpoint");
			logger.error(messageError);
			throw new ApplicationFailure(messageError);
		}

		String home = homeEndpoint.get();
		logger.info("\n\n***********************  " + home + "  ***********************\n");
	}
}
