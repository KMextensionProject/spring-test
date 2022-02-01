package sk.golddigger.messaging;

import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Component
public class EmailProvider {

	@Value("${smtp_host}")
	private String smtpHost;

	// how to inject int value?
	@Value("${port}")
	private String port;

	@Value("${username}")
	private String username;

	@Value("${password}")
	private String password;

	public JavaMailSender configure() {

		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(smtpHost);
		mailSender.setUsername(username);
		mailSender.setPassword(password);
		mailSender.setPort(Integer.parseInt(port));

		Properties mailProperties = new Properties();
		mailProperties.put("mail.smtp.starttls.enable", true);
		mailProperties.put("mail.smtp.ssl.trust", smtpHost);

		mailSender.setJavaMailProperties(mailProperties);
		return mailSender;
	}

	@PostConstruct
	private void validateConfigurationPresence() {
		// if those values are not set, log that it was not configured and
		// do not force this setting to prevent the application to run
	}
}
