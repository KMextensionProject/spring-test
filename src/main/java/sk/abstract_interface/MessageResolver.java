package sk.abstract_interface;

import java.io.IOException;
import java.util.Properties;

public final class MessageResolver {

	private static Properties properties = new Properties();

	static {
		try {
			properties.load(MessageResolver.class.getClassLoader().getResourceAsStream(Resources.MESSAGES_MAP));
		} catch (IOException readingFailure) {
			readingFailure.printStackTrace();
		}
	}

	public static String resolveMessage(String property, Object... messageArguments) {
		String message = properties.getProperty(property, property);
		if (messageArguments.length != 0) {
			message = insertArguments(message, messageArguments);
		}
		return message;
	}

	private static String insertArguments(String message, Object[] messageArguments) {
		for (int i = 0; i < messageArguments.length; i++) {
			message = message.replace("{" + i +"}", messageArguments[i].toString());
		}
		return message;
	}
}
