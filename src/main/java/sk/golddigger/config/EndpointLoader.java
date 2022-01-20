package sk.golddigger.config;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
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
	private String ipAddress;

	public EndpointLoader() {
		this.ipAddress = findIpAddress();
		this.endpoints = new HashSet<>(10);
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
		endpoint.append(ipAddress);//.getHostAddress());
		endpoint.append(ENDPOINT_PORT);
		endpoint.append(ENDPOINT_APP_NAME);
		endpoint.append(directPaths.toString().substring(1));
		endpoint.deleteCharAt(endpoint.length() - 1);

		return endpoint.toString();
	}

	public Set<String> getApplicationEndpoints() {
		return new HashSet<>(this.endpoints);
	}

	private static String findIpAddress() {
		Enumeration<NetworkInterface> networkInterfaces = null;

		try {
			networkInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException error) {
			String message = resolveMessage("ipResolveError", error);
			logger.error(message);
			throw new ApplicationFailure(message, error);
		}

		while (networkInterfaces.hasMoreElements()) {
			NetworkInterface ni = (NetworkInterface) networkInterfaces.nextElement();
			if (ni.getName().equalsIgnoreCase("wlp2s0")) {
				Enumeration<InetAddress> nias = ni.getInetAddresses();

				while (nias.hasMoreElements()) {
					InetAddress ia = (InetAddress) nias.nextElement();
					if (!ia.isLinkLocalAddress() && !ia.isLoopbackAddress() && ia instanceof Inet4Address) {
						return ia.getHostAddress();
					}
				}
			}
		}
		return null;
	}
}