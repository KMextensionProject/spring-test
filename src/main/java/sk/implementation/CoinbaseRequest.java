package sk.implementation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
import sk.abstract_interface.Resources;
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

	private List<Header> defaultHeaders;

	// TODO: centralize the time
	@Override
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getAllAccounts() throws Exception {
		String requestPath = urlResolver.getRequestPath(Resources.COINBASE_ACCOUNTS_URL);
		long epochSeconds = ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond();
		String signature = computeSignature(epochSeconds, HttpMethod.GET, requestPath);

		List<Header> headers = addRequestHeaders(epochSeconds, signature);
		String responseBody = getJson(Resources.COINBASE_ACCOUNTS_URL, headers);

		Gson gson = new Gson();
		List<Map<String, Object>> result = gson.fromJson(responseBody, List.class);

		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public double getAccountBalance(String accountId) throws Exception {
		String url = urlResolver.resolve(Resources.COINBASE_ACCOUNT_BY_ID_URL, accountId);
		String requestPath = urlResolver.getRequestPath(url);
		long epochSeconds = ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond();
		String signature = computeSignature(epochSeconds, HttpMethod.GET, requestPath);

		List<Header> headers = addRequestHeaders(epochSeconds, signature);
		String responseBody = getJson(url, headers);

		Gson gson = new Gson();
		Map<String, Object> result = gson.fromJson(responseBody, Map.class);

		return Double.valueOf(String.valueOf(result.get("balance")));
	}

	private final String computeSignature(long epochSeconds, String httpMethod, String requestPath) {
		return computeSignature(epochSeconds, httpMethod, requestPath, null);
	}

	private final String computeSignature(long epochSeconds, String httpMethod, String requestPath, String jsonBody) {
		String preHash = epochSeconds + httpMethod + requestPath + (jsonBody != null ? jsonBody : "");
		byte[] decodedSecretKey = encoder.decodeBase64(environment.getProperty("COINBASE-VIEW-API-SECRET").getBytes());
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
			defaultHeaders.add(new BasicHeader("cb-access-key", environment.getProperty("COINBASE-VIEW-API-KEY")));
			defaultHeaders.add(new BasicHeader("cb-access-passphrase", environment.getProperty("COINBASE-VIEW-API-PASSPHRASE")));
		}
		// default headers should contain only static elements without modification
		return new ArrayList<>(defaultHeaders);
	}

	private String getJson(String url, List<Header> headers) throws ClientProtocolException, IOException {
		HttpGet request = new HttpGet(url);
		request.addHeader(HttpHeaders.ACCEPT, Resources.APPLICATION_JSON);
		headers.forEach(request::addHeader);

		HttpResponse response = client.execute(request);
		HttpEntity body = response.getEntity();

		// leaves the stream open when done with copying
//		String jsonBody = StreamUtils.copyToString(body.getContent(), StandardCharsets.UTF_8);
		return EntityUtils.toString(body, StandardCharsets.UTF_8);
	}

}
