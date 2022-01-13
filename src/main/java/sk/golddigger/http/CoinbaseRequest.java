package sk.golddigger.http;

import static sk.golddigger.enums.Resources.COINBASE_ACCOUNTS_URL;
import static sk.golddigger.enums.Resources.COINBASE_ACCOUNT_BY_ID_URL;
import static sk.golddigger.enums.Resources.COINBASE_ORDER_FILLS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import sk.golddigger.core.ExchangeRequest;
import sk.golddigger.core.RequestDateTime;
import sk.golddigger.enums.Currency;
import sk.golddigger.utils.EncoderUtils;
import sk.golddigger.utils.URLResolver;

@Component
public class CoinbaseRequest extends DefaultHttpRequest implements ExchangeRequest {

	@Autowired
	private Environment environment;

	@Autowired
	private EncoderUtils encoder;

	@Autowired
	private URLResolver urlResolver;

	@Autowired
	@Qualifier("accountCurrency")
	private Currency accountCurrency;

	@Autowired
	@Qualifier("tradingCurrency")
	private Currency tradingCurrency;

	@Autowired
	private RequestDateTime requestTime;

	@Autowired
	private Gson gson;

	private List<Header> defaultHeaders;

	@Override
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getAllAccounts() throws Exception {
		List<Header> headers = computeRequestHeaders(COINBASE_ACCOUNTS_URL, HttpMethod.GET);
		String responseBody = getJson(COINBASE_ACCOUNTS_URL, headers);
		List<Map<String, Object>> result = gson.fromJson(responseBody, List.class);

		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getAllOrderFills() throws Exception {
		String url = urlResolver.resolveParams(COINBASE_ORDER_FILLS, tradingCurrency.getAcronym(), accountCurrency.getAcronym());
		List<Header> headers = computeRequestHeaders(url, HttpMethod.GET);
		String responseBody = getJson(url, headers);
		List<Map<String, Object>> fills = gson.fromJson(responseBody, List.class);

		return fills;
	}

	@Override
	@SuppressWarnings("unchecked")
	public double getAccountBalance(String accountId) throws Exception {
		String url = urlResolver.resolveParams(COINBASE_ACCOUNT_BY_ID_URL, accountId);
		List<Header> headers = computeRequestHeaders(url, HttpMethod.GET);
		String responseBody = getJson(url, headers);
		Map<String, Object> result = gson.fromJson(responseBody, Map.class);

		return Double.valueOf(String.valueOf(result.get("balance")));
	}

	/**
	 * Tato metoda vytvori podpis z URL adresy a aktualneho casu v sekundach UTC.
	 * Nasledne prida tento podpis s casom do zoznamu hlaviciek HTTP poziadavky.
	 */
	private List<Header> computeRequestHeaders(String url, String httpMethod) {
		String requestPath = urlResolver.resolvePath(url);
		long timestamp = requestTime.getEpochSecondsUTC();
		String signature = computeSignature(timestamp, httpMethod, requestPath);

		return addRequestHeaders(timestamp, signature);
	}

	private final String computeSignature(long timestamp, String httpMethod, String requestPath) {
		return computeSignature(timestamp, httpMethod, requestPath, null);
	}

	private final String computeSignature(long timestamp, String httpMethod, String requestPath, String jsonBody) {
		String preHash = timestamp + httpMethod + requestPath + (jsonBody != null ? jsonBody : "");
		byte[] decodedSecretKey = encoder.decodeBase64(environment.getProperty("COINBASE-API-SECRET").getBytes());
		byte[] signature = encoder.encodeHmacSha256(decodedSecretKey, preHash.getBytes());

		return new String(encoder.encodeBase64(signature));		
	}

	private final List<Header> addRequestHeaders(long timestamp, String signature) {
		List<Header> defaultHeaders = getDefaultHeaders();
		defaultHeaders.add(new BasicHeader("cb-access-sign", signature));
		defaultHeaders.add(new BasicHeader("cb-access-timestamp", String.valueOf(timestamp)));
		return defaultHeaders;
	}

	private final List<Header> getDefaultHeaders() {
		if (defaultHeaders == null) {
			defaultHeaders = new ArrayList<Header>(5);
			defaultHeaders.add(new BasicHeader("cb-access-key", environment.getProperty("COINBASE-API-KEY")));
			defaultHeaders.add(new BasicHeader("cb-access-passphrase", environment.getProperty("COINBASE-API-PASSPHRASE")));
		}
		// default headers should contain only static elements without modification
		return new ArrayList<>(defaultHeaders);
	}

}
