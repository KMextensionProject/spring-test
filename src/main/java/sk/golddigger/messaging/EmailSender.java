package sk.golddigger.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {

	private JavaMailSender emailSender;

	@Autowired
	public EmailSender(EmailProvider emailProvider) {
		this.emailSender = emailProvider.configure();
	}

	public void send(Email email) throws MailException {
		emailSender.send(email);
	}
}
