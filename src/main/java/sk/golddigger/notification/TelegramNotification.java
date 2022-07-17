package sk.golddigger.notification;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sk.golddigger.annotations.OnPropertyContent;
import sk.golddigger.enums.RegexPatterns;
import sk.golddigger.http.TelegramRequest;
import sk.golddigger.messaging.Message;
import sk.golddigger.messaging.Recipient;

@OnPropertyContent(propertyName = "NOTIFICATION_RECIPIENT", lookupRegex = RegexPatterns.TELEGRAM)
@Component
public class TelegramNotification implements Notification {

	private static final Logger logger = Logger.getLogger(TelegramNotification.class);

	@Autowired
	private TelegramRequest telegramService;

	@Override
	public void send(Message message, Recipient recipient) {
		String msg = "<b>" + message.getSubject() + "</b>" + System.lineSeparator() + message.getBody();
		String otherAddress = recipient.getOtherAddress();
		String botId = otherAddress.substring(0, otherAddress.lastIndexOf(":")); 
		String chatId = otherAddress.substring(otherAddress.lastIndexOf(":") + 1);
		telegramService.sendTelegramMessage(botId, chatId, msg);
	}

	@PostConstruct
	public void logInit() {
		logger.info(resolveMessage("notificationInitialized", TelegramNotification.class.getSimpleName()));
	}
}
