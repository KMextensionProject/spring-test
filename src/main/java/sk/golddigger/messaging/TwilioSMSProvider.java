package sk.golddigger.messaging;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.twilio.Twilio;

@Lazy
@Component
public class TwilioSMSProvider extends SMSProvider {

	@PostConstruct
	private void init() {
		Twilio.init(username, password);
	}
}
