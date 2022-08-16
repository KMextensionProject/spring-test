package sk.golddigger.http;
import static sk.golddigger.utils.MessageResolver.resolveMessage;

import java.net.URLEncoder;
import static java.nio.charset.StandardCharsets.UTF_8;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jayway.jsonpath.JsonPath;

import sk.golddigger.enums.Resources;
import sk.golddigger.utils.URLResolver;

@Component
public class TelegramRequest extends DefaultHttpRequest {

	private static final Logger logger = Logger.getLogger(TelegramRequest.class);

	private static final String CHAT_TITLE_JSON_PATH = "$.result.chat.title";

	@Autowired
	private URLResolver urlResolver;

	public void sendTelegramMessage(String botId, String chatId, String message) {
		String url = urlResolver.resolveParams(Resources.TELEGRAM_SEND_MASSAGE_URL, botId, chatId, URLEncoder.encode(message, UTF_8));
		String response = getJson(url, null);

		String groupName = JsonPath.compile(CHAT_TITLE_JSON_PATH).read(response);
		logger.info(resolveMessage("telegramMessageSent", groupName));
	}
}
