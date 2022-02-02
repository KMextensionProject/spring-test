package sk.golddigger.notification;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Component;

import sk.golddigger.messaging.Email;
import sk.golddigger.messaging.EmailSender;
import sk.golddigger.messaging.Message;
import sk.golddigger.messaging.Recipient;

@Component
public class EmailNotification implements Notification {

	private static final Logger logger = Logger.getLogger(EmailNotification.class);

	@Autowired
	private EmailSender emailSender;

	@Override
	public void send(Message message, Recipient recipient) {
		String subject = message.getSubject();
		String body = message.getBody();
		String recipientEmail = recipient.getEmail();

		try {
			Email email = new Email(recipientEmail, subject, body);
			boolean notificationSent = emailSender.send(email);

			if (notificationSent) {
				logger.info(resolveMessage("emailNotificationSuccessful", recipientEmail));
			} else {
				logger.warn(resolveMessage("emailNotificationUnsuccessful", recipientEmail));
			}

		} catch (MailAuthenticationException mailAuthException) {
			logger.error(resolveMessage("emailAuthenticaionFailure", mailAuthException.getMessage()));
		} catch (MailException mailException) {
			logger.error(resolveMessage("emailSendFailure", mailException.getMessage()));
		}
	}
}
