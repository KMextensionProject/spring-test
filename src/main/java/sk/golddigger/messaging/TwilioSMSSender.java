package sk.golddigger.messaging;

import static com.twilio.rest.api.v2010.account.Message.creator;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.twilio.exception.TwilioException;
import com.twilio.type.PhoneNumber;

@Lazy
@Component
public class TwilioSMSSender extends SMSSender {

	private static final Logger logger = Logger.getLogger(TwilioSMSSender.class);

	@Autowired
	private TwilioSMSProvider smsProvider;

	@Override
	public boolean send(Message message, Recipient recipient) {
		PhoneNumber from = new PhoneNumber(smsProvider.getSourceNumber());
		PhoneNumber to = new PhoneNumber(recipient.getPhoneNumber());
		try {
			creator(to, from, message.getBody());
			return true;
		} catch (TwilioException ex) {
			logger.warn("SMS could not be sent: " + ex.getMessage());
			return false;
		}
	}
}
