package sk.golddigger.notification;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import sk.golddigger.annotations.OnPropertyContent;
import sk.golddigger.messaging.Message;
import sk.golddigger.messaging.Recipient;

@OnPropertyContent(propertyName = "NOTIFICATION_RECIPIENT", lookupValue = OnPropertyContent.EMPTY)
@Component
public class NoOpNotification implements Notification {

	private static final Logger logger = Logger.getLogger(NoOpNotification.class);

	public NoOpNotification() {
		logger.info(resolveMessage("notificationInitialized", NoOpNotification.class.getSimpleName()));
	}

	@Override
	public void send(Message message, Recipient recipient) {
		// designed not to perform any operation
	}

}
