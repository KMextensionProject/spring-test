package sk.golddigger.messaging;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Lazy
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
			try {
				javaMailSender.send(email);
				return true;
			} catch (MailAuthenticationException mailAuthException) {
				logger.error(resolveMessage("emailAuthenticaionFailure", mailAuthException.getMessage()));
			} catch (MailException mailException) {
				logger.error(resolveMessage("emailSendFailure", mailException.getMessage()));
			}
		}
		return false;
	}
}
