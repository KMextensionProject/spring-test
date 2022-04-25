package sk.golddigger.enums;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

public class RegexPatterns {

	public static final String EMAIL = "^\\S+@\\S+$";
	public static final String PHONE = "^\\+[1-9]{1}[0-9]{3,14}$";
	public static final String NULL = "null";

	private RegexPatterns() {
		throw new IllegalStateException(resolveMessage("factoryClassInstantiationError", RegexPatterns.class));
	}

}
