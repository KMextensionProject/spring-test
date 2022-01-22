package sk.golddigger.enums;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

public class ContentType {

	private ContentType() {
		throw new IllegalStateException(resolveMessage("factoryClassInstantiationError", ContentType.class));
	}

	public static final String APPLICATION_JSON = "application/json";
	public static final String TEXT_PLAIN = "text/plain";

}
