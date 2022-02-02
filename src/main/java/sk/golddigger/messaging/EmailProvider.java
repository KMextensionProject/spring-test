package sk.golddigger.messaging;

import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Component
public class EmailProvider {

	@Value("${email_smtp_host}")
	private String smtpHost;

	@Value("${email_port}")
	private String port;

	@Value("${email_username}")
	private String username;

	@Value("${email_password}")
	private String password;

	private boolean isMailSenderConfigured;

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

	public boolean hasValidConfiguration() {
		return this.isMailSenderConfigured;
	}

	@PostConstruct
	private void checkConfigurationParamsPresence() {
		this.isMailSenderConfigured = (smtpHost != null && !smtpHost.isEmpty()) && StringUtils.isNumeric(port);
	}
}
