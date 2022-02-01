package sk.golddigger.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {

	private JavaMailSender javaMailSender;

	@Autowired
	public EmailSender(EmailProvider emailProvider) {
		this.javaMailSender = emailProvider.configure();
	}

	public void send(Email email) throws MailException {
		javaMailSender.send(email);
	}
}
