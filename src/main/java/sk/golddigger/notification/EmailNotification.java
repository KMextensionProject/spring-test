package sk.golddigger.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sk.golddigger.messaging.Email;
import sk.golddigger.messaging.EmailSender;
import sk.golddigger.messaging.Message;
import sk.golddigger.messaging.Recipient;

@Component
public class EmailNotification implements Notification {

	@Autowired
	private EmailSender emailSender;

	@Override
	public void send(Message message, Recipient recipient) {
		String subject = message.getSubject();
		String body = message.getBody();
		String email = recipient.getEmail();

		emailSender.send(new Email(email, subject, body));
	}

}
