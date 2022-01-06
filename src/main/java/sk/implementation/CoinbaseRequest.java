package sk.implementation;

import static java.nio.charset.StandardCharsets.UTF_8;
import static sk.abstract_interface.Resources.APPLICATION_JSON;
import static sk.abstract_interface.Resources.COINBASE_ACCOUNTS_URL;
import static sk.abstract_interface.Resources.COINBASE_ACCOUNT_BY_ID_URL;
import static sk.abstract_interface.Resources.COINBASE_ORDER_FILLS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import sk.abstract_interface.ExchangeRequest;
import sk.abstract_interface.URLResolver;

@Component
public class CoinbaseRequest implements ExchangeRequest {

	@Autowired
	private Environment environment;

	@Autowired
	private EncoderUtils encoder;

	@Autowired
	private URLResolver urlResolver;

	@Autowired
	private HttpClient client;

	@Autowired
	private RequestTime requestTime;

	@Autowired
	private Gson gson;

	private List<Header> defaultHeaders;

	@Override
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getAllAccounts() throws Exception {
		String requestPath = urlResolver.resolvePath(COINBASE_ACCOUNTS_URL);
		long timestamp = requestTime.getEpochSecondsUTC();
		String signature = computeSignature(timestamp, HttpMethod.GET, requestPath);

		List<Header> headers = addRequestHeaders(timestamp, signature);
		String responseBody = getJson(COINBASE_ACCOUNTS_URL, headers);
		List<Map<String, Object>> result = gson.fromJson(responseBody, List.class);

		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getAllOrderFills() throws Exception {
		String requestPath = urlResolver.resolvePath(COINBASE_ORDER_FILLS);
		long timestamp = requestTime.getEpochSecondsUTC();
		String signature = computeSignature(timestamp, HttpMethod.GET, requestPath);

		List<Header> headers = addRequestHeaders(timestamp, signature);
		String responseBody = getJson(COINBASE_ORDER_FILLS, headers);
		List<Map<String, Object>> fills = gson.fromJson(responseBody, List.class);

		return fills;
	}

	@Override
	@SuppressWarnings("unchecked")
	public double getAccountBalance(String accountId) throws Exception {
		String url = urlResolver.resolveParams(COINBASE_ACCOUNT_BY_ID_URL, accountId);
		String requestPath = urlResolver.resolvePath(url);
		long timestamp = requestTime.getEpochSecondsUTC();
		String signature = computeSignature(timestamp, HttpMethod.GET, requestPath);

		List<Header> headers = addRequestHeaders(timestamp, signature);
		String responseBody = getJson(url, headers);
		Map<String, Object> result = gson.fromJson(responseBody, Map.class);

		return Double.valueOf(String.valueOf(result.get("balance")));
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

	private String getJson(String url, List<Header> headers) throws ClientProtocolException, IOException {
		HttpGet request = new HttpGet(url);
		request.addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON);
		headers.forEach(request::addHeader);

		HttpResponse response = client.execute(request);
		HttpEntity body = response.getEntity();

		// leaves the stream open when done with copying
//		String jsonBody = StreamUtils.copyToString(body.getContent(), StandardCharsets.UTF_8);
		return EntityUtils.toString(body, UTF_8);
	}

}
