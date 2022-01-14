package sk.golddigger.utils;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import sk.golddigger.enums.Resources;

/**
 * This class enables to have user friendly messages apart from the code and thus 
 * enhances the code readability and length.</br>
 * When this class gets loaded, the content of the messages.properties file is
 * loaded into memory and stays cached for fase retrieval.
 * 
 * @author mkrajcovic
 */
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

	/**
	 * Performs message lookup based on the specified key.
	 * If it finds the message value, it then tries to fill in the
	 * specified {@code messageArguments} one by one.
	 * 
	 * @param property - key to the message
	 * @param messageArguments - arguments to add on substitution positions in the message
	 * @return the resolved message or the original message when the value is not found
	 */
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
