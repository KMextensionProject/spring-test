package sk.golddigger.notification;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sk.golddigger.annotations.OnPropertyContent;
import sk.golddigger.enums.RegexPatterns;
import sk.golddigger.messaging.Message;
import sk.golddigger.messaging.Recipient;
import sk.golddigger.messaging.SMSSender;

@OnPropertyContent(propertyName = "NOTIFICATION_RECIPIENT", lookupRegex = RegexPatterns.PHONE)
@Component
public class SMSNotification implements Notification {

	private static final Logger logger = Logger.getLogger(SMSNotification.class);

	@Autowired
	private SMSSender smsSender;

	@Override
	public void send(Message message, Recipient recipient) {
		boolean notificationSent = smsSender.send(message, recipient);

		// TODO: add this to messages.properties
		if (notificationSent) {
			logger.info(resolveMessage("smsNotificationSuccessful", recipient.getPhoneNumber()));
		} else {
			logger.warn(resolveMessage("smsNotificationUnsuccessful", recipient.getPhoneNumber()));
		}
	}

	@PostConstruct
	public void logInit() {
		logger.info(resolveMessage("notificationInitialized", SMSNotification.class.getSimpleName()));
	}
}
