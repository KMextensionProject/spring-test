package sk.golddigger.messaging;

import org.springframework.beans.factory.annotation.Value;

public abstract class SMSProvider {

	@Value("${sms_username}")
	protected String username;

	@Value("${sms_password}")
	protected String password;

	@Value("${sms_source_number}")
	protected String sourceNumber;

	public String getSourceNumber() {
		return this.sourceNumber;
	}
}
