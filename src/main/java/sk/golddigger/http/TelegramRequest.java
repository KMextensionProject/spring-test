package sk.golddigger.http;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sk.golddigger.enums.Resources;
import sk.golddigger.utils.URLResolver;

@Component
public class TelegramRequest extends DefaultHttpRequest {

	@Autowired
	private URLResolver urlResolver;

	public void sendTelegramMessage(String botId, String chatId, String message) {
		String url = urlResolver.resolveParams(Resources.TELEGRAM_SEND_MASSAGE_URL, botId, chatId, URLEncoder.encode(message, StandardCharsets.UTF_8));
		getJson(url, null);
	}
}
