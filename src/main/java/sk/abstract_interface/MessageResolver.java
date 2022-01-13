package sk.abstract_interface;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public final class MessageResolver {
	
	private static final Logger logger = Logger.getLogger(MessageResolver.class);

	private static Properties properties = new Properties();

	static {
		try {
			properties.load(MessageResolver.class.getClassLoader().getResourceAsStream(Resources.MESSAGES_MAP));
			logger.info(resolveMessage("messageResolver"));
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
