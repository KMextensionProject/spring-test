package sk.golddigger.messaging;

import org.springframework.mail.SimpleMailMessage;

@SuppressWarnings("serial")
public class Email extends SimpleMailMessage {

	public Email(String recipient, String subject, String message) {
		setTo(recipient);
		setSubject(subject);
		setText(message);
	}
}
