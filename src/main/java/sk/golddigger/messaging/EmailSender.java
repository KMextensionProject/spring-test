package sk.golddigger.messaging;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {

	private static final Logger logger = Logger.getLogger(EmailSender.class);

	private JavaMailSender javaMailSender;

	@Autowired
	public EmailSender(EmailProvider emailProvider) {
		if (emailProvider.hasValidConfiguration()) {
			this.javaMailSender = emailProvider.configure();
			logger.info(resolveMessage("emailProviderConfigured"));
		} else {
			logger.warn(resolveMessage("emailProviderNotConfigured"));
		}
	}

	public boolean send(Email email) throws MailAuthenticationException, MailException {
		if (javaMailSender != null) {
			javaMailSender.send(email);
			return true;
		}
		return false;
	}
}
