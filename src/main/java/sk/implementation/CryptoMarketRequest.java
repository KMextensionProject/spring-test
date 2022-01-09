package sk.implementation;

import static java.nio.charset.StandardCharsets.UTF_8;
import static sk.abstract_interface.Resources.APPLICATION_JSON;
import static sk.abstract_interface.Resources.BITCOIN_PRICE_BY_DATE_URL;
import static sk.abstract_interface.Resources.CURRENT_BITCOIN_PRICE_URL;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import sk.abstract_interface.Currency;
import sk.abstract_interface.MarketRequest;
import sk.abstract_interface.PriceType;
import sk.abstract_interface.URLResolver;

@Component
public final class BitcoinRequest implements MarketRequest {

	@Autowired
	private URLResolver urlResolver;

	@Autowired
	private Currency accountCurrency;

	@Autowired
	private HttpClient client;

	@Autowired
	private Gson gson;

	@Value("${polygon.api_key}")
	private String polygonApiKey;

	@Override
	@SuppressWarnings("unchecked")
	public double getCurrentPrice() throws IOException {
		String url = urlResolver.resolveParams(CURRENT_BITCOIN_PRICE_URL, accountCurrency.getAcronym());
		String jsonBody = getJson(url);

		Map<String, Object> responseMap = gson.fromJson(jsonBody, Map.class);
		Map<String, Object> dataElement = (Map<String, Object>) responseMap.get("data");
		String amountString = String.valueOf(dataElement.get("amount"));

		return Double.valueOf(amountString).doubleValue();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<PriceType, Double> getPricesByDate(LocalDate date) throws IOException {
		String url = urlResolver.resolveParams(BITCOIN_PRICE_BY_DATE_URL, accountCurrency.getAcronym(), date, date, polygonApiKey);
		String jsonBody = getJson(url);

		Map<String, Object> topLevelObject = gson.fromJson(jsonBody, Map.class);
		List<Map<String, Object>> data = (List<Map<String, Object>>) topLevelObject.get("results");

		Map<PriceType, Double> result = new HashMap<>();

		for (PriceType type : PriceType.values()) {
			String price = String.valueOf(data.get(0).get(type.getMark()));
			result.put(type, Double.valueOf(price));
		}

		return result;
	}

	// TODO: extract this duplicate method
	private String getJson(String url) throws ClientProtocolException, IOException {
		HttpGet request = new HttpGet(url);
		request.addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON);

		HttpResponse response = client.execute(request);
		HttpEntity body = response.getEntity();

		// leaves the stream open when done with copying
//		String jsonBody = StreamUtils.copyToString(body.getContent(), StandardCharsets.UTF_8);
		return EntityUtils.toString(body, UTF_8);
	}

}
