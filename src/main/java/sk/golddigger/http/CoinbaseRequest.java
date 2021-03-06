package sk.golddigger.http;

import static java.nio.charset.StandardCharsets.UTF_8;
import static sk.golddigger.enums.HttpMethod.GET;
import static sk.golddigger.enums.HttpMethod.POST;
import static sk.golddigger.enums.Resources.COINBASE_ACCOUNTS_URL;
import static sk.golddigger.enums.Resources.COINBASE_ACCOUNT_BY_ID_URL;
import static sk.golddigger.enums.Resources.COINBASE_ORDER_BY_ID_URL;
import static sk.golddigger.enums.Resources.COINBASE_ORDER_FILLS;
import static sk.golddigger.enums.Resources.COINBASE_PLACE_ORDER_URL;
import static sk.golddigger.utils.EncoderUtils.decodeBase64;
import static sk.golddigger.utils.EncoderUtils.encodeBase64;
import static sk.golddigger.utils.EncoderUtils.encodeHmacSha256;
import static sk.golddigger.utils.MessageResolver.resolveMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import sk.golddigger.core.ExchangeRequest;
import sk.golddigger.core.Order;
import sk.golddigger.core.RequestDateTime;
import sk.golddigger.enums.Currency;
import sk.golddigger.utils.URLResolver;

@Component
public class CoinbaseRequest extends DefaultHttpRequest implements ExchangeRequest {

	private static final Logger logger = Logger.getLogger(CoinbaseRequest.class);

	@Value("${COINBASE_API_KEY:null}")
	private String apiKey;

	@Value("${COINBASE_API_SECRET:null}")
	private String apiSecret;

	@Value("${COINBASE_API_PASSPHRASE:null}")
	private String apiPassphrase;

	@Autowired
	@Qualifier("accountCurrency")
	private Currency accountCurrency;

	@Autowired
	@Qualifier("tradingCurrency")
	private Currency tradingCurrency;

	@Autowired
	private URLResolver urlResolver;

	@Autowired
	private RequestDateTime requestTime;

	@Autowired
	private Gson gson;

	private List<Header> defaultHeaders;

	@Override
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getAllAccounts() {
		List<Header> headers = computeRequestHeaders(COINBASE_ACCOUNTS_URL, GET, null);
		String responseBody = getJson(COINBASE_ACCOUNTS_URL, headers);
		logPayload(responseBody);

		return gson.fromJson(responseBody, List.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getAllOrderFills() {
		String url = urlResolver.resolveParams(COINBASE_ORDER_FILLS, tradingCurrency.getAcronym(), accountCurrency.getAcronym());
		List<Header> headers = computeRequestHeaders(url, GET, null);
		String responseBody = getJson(url, headers);
		logPayload(responseBody);

		return gson.fromJson(responseBody, List.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> getOrderById(String orderId) {
		String url = urlResolver.resolveParams(COINBASE_ORDER_BY_ID_URL, orderId);
		List<Header> headers = computeRequestHeaders(url, GET, null);
		String responseBody = getJson(url, headers);
		logPayload(responseBody);

		return gson.fromJson(responseBody, Map.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public double getAccountBalance(String accountId) {
		String url = urlResolver.resolveParams(COINBASE_ACCOUNT_BY_ID_URL, accountId);
		List<Header> headers = computeRequestHeaders(url, GET, null);
		String responseBody = getJson(url, headers);
		Map<String, Object> result = gson.fromJson(responseBody, Map.class);
		logPayload(responseBody);

		return Double.valueOf(String.valueOf(result.get("balance")));
	}

	@Override
	@SuppressWarnings("unchecked")
	public String postOrder(Order order) {
		String requestBody = gson.toJson(order);
		logPayload(requestBody);

		List<Header> headers = computeRequestHeaders(COINBASE_PLACE_ORDER_URL, POST, requestBody);
		String responseBody = postJson(COINBASE_PLACE_ORDER_URL, headers, requestBody);
		Map<String, Object> result = gson.fromJson(responseBody, Map.class);
		logPayload(responseBody);

		return String.valueOf(result.get("id"));
	}

	/**
	 * Tato metoda vytvori podpis z URL adresy a aktualneho casu v sekundach UTC.
	 * Nasledne prida tento podpis s casom do zoznamu hlaviciek HTTP poziadavky.
	 */
	private List<Header> computeRequestHeaders(String url, String httpMethod, String jsonBody) {
		String requestPath = urlResolver.resolvePath(url);
		long timestamp = requestTime.getEpochSecondsUTC();
		String signature = computeSignature(timestamp, httpMethod, requestPath, jsonBody);

		return addRequestHeaders(timestamp, signature);
	}

	private final String computeSignature(long timestamp, String httpMethod, String requestPath, String jsonBody) {
		String preHash = timestamp + httpMethod + requestPath + (jsonBody != null ? jsonBody : "");
		byte[] decodedSecretKey = decodeBase64(apiSecret.getBytes()); // NOSONAR (validation in @PostConstruct)
		byte[] signature = encodeHmacSha256(decodedSecretKey, preHash.getBytes());

		return new String(encodeBase64(signature), UTF_8);
	}

	private final List<Header> addRequestHeaders(long timestamp, String signature) {
		List<Header> headers = getDefaultHeaders();
		headers.add(new BasicHeader("cb-access-sign", signature));
		headers.add(new BasicHeader("cb-access-timestamp", String.valueOf(timestamp)));
		return headers;
	}

	private final List<Header> getDefaultHeaders() {
		if (defaultHeaders == null) {
			defaultHeaders = new ArrayList<>(5);
			defaultHeaders.add(new BasicHeader("cb-access-key", apiKey));
			defaultHeaders.add(new BasicHeader("cb-access-passphrase", apiPassphrase));
		}
		// default headers should contain only static elements without modification
		return new ArrayList<>(defaultHeaders);
	}

	@PostConstruct
	private void validateCoinbaseAPIEnv() {
		final String NULL = "null";
		if (NULL.equals(apiKey)|| NULL.equals(apiSecret) || NULL.equals(apiPassphrase)) {
			logger.error(resolveMessage("missingCoinbaseKeys"));
			System.exit(1);
		}
		logger.info(resolveMessage("coinbaseKeysValidation"));
	}
}
