package sk.golddigger.enums;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

public class HttpMethod {

	private HttpMethod() {
		throw new IllegalStateException(resolveMessage("factoryClassInstantiationError", HttpMethod.class));
	}

	public static final String GET = "GET";
	public static final String PUT = "PUT";
	public static final String POST = "POST";
	public static final String HEAD = "HEAD";
	public static final String DELETE = "DELETE";

}
