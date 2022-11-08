package sk.golddigger.utils;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import java.util.UUID;

public class UUIDHelper {

	private UUIDHelper() {
		throw new IllegalStateException(resolveMessage("factoryClassInstantiationError", UUIDHelper.class));
	}

	public static String generateRandomUUID() {
		return UUID.randomUUID().toString();
	}

	public static String generateRandomUUIDnoDashes() {
		return generateRandomUUID().replace("-", "");
	}

}
