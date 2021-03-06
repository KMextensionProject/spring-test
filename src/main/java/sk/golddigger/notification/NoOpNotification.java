package sk.golddigger.notification;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import sk.golddigger.annotations.OnPropertyContent;
import sk.golddigger.enums.RegexPatterns;
import sk.golddigger.messaging.Message;
import sk.golddigger.messaging.Recipient;

@OnPropertyContent(propertyName = "NOTIFICATION_RECIPIENT", lookupRegex = RegexPatterns.NULL)
@Component
public class NoOpNotification implements Notification {

	private static final Logger logger = Logger.getLogger(NoOpNotification.class);

	@Override
	public void send(Message message, Recipient recipient) {
		// designed not to perform any operation
	}

	@PostConstruct
	public void logInit() {
		logger.info(resolveMessage("notificationInitialized", NoOpNotification.class.getSimpleName()));
	}
}
