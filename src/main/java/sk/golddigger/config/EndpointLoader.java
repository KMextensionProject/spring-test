package sk.golddigger.config;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;

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
	private static final int PRIVATE_IP_LENGTH = 13;

	private Set<String> endpoints;
	private String ipAddress;

	public EndpointLoader() {
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

	@PostConstruct
	private void initializeIpAddress() {
		Enumeration<NetworkInterface> networkInterfaces = null;

		try {
			networkInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException error) {
			String message = resolveMessage("ipResolveError", error);
			logger.error(message);
			throw new ApplicationFailure(message, error);
		}

		this.ipAddress = lookupServerIpAddress(networkInterfaces);
	}

	/*
	 * vratit optional <String>
	 */
	private String lookupServerIpAddress(Enumeration<NetworkInterface> networkInterfaces) {
		while (networkInterfaces.hasMoreElements()) {
			NetworkInterface networkInterface = (NetworkInterface) networkInterfaces.nextElement();
			Enumeration<InetAddress> networkAddresses = networkInterface.getInetAddresses();

			Optional<String> privateIp = checkForPrivateIpAddress(networkAddresses);
			if (privateIp.isPresent()) {
				return privateIp.get();
			}
		}

		String serverIpNotFoundMessage = resolveMessage("serverIpNotFound");
		if (logger.isDebugEnabled()) {
			logger.debug(serverIpNotFoundMessage);
		}

		throw new ApplicationFailure(serverIpNotFoundMessage);
	}

	private Optional<String> checkForPrivateIpAddress(Enumeration<InetAddress> networkAddresses) {
		while (networkAddresses.hasMoreElements()) {

			InetAddress ip = (InetAddress) networkAddresses.nextElement();
			if (!ip.isLinkLocalAddress() && !ip.isLoopbackAddress() && ip instanceof Inet4Address) {

				String address = ip.getHostAddress();
				if (address.length() >= PRIVATE_IP_LENGTH) {

					if (logger.isDebugEnabled()) {
						logger.debug(resolveMessage("serverIpFound", address));
					}

					return Optional.of(address);
				}
			}
		}
		return Optional.empty();
	}
}
