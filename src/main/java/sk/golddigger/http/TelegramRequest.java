package sk.golddigger.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sk.golddigger.enums.Resources;
import sk.golddigger.utils.URLResolver;

@Component
public class TelegramRequest extends DefaultHttpRequest {

	@Autowired
	private URLResolver urlResolver;

	public boolean sendTelegramMessage(String botId, String chatId, String message) {
		String url = urlResolver.resolveParams(Resources.TELEGRAM_SEND_MASSAGE_URL, botId, chatId, message);
		String json = getJson(url, null);

		if (json.contains("OK")) {
			return true;
		}
		return false;
	}
}
