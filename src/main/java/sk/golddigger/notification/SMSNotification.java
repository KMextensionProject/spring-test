package sk.golddigger.notification;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import sk.golddigger.annotations.OnPropertyContent;
import sk.golddigger.enums.RegexPatterns;
import sk.golddigger.messaging.Message;
import sk.golddigger.messaging.Recipient;

@OnPropertyContent(propertyName = "NOTIFICATION_RECIPIENT", lookupRegex = RegexPatterns.PHONE)
@Component
public class SMSNotification implements Notification {

	private static final Logger logger = Logger.getLogger(SMSNotification.class);

	@Override
	public void send(Message message, Recipient recipient) {
		// TODO: implement Twillio provider
	}

	@PostConstruct
	public void logInit() {
		logger.info(resolveMessage("notificationInitialized", SMSNotification.class.getSimpleName()));
	}
}
