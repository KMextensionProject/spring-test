package sk.implementation;

import static java.nio.charset.StandardCharsets.UTF_8;
import static sk.abstract_interface.Resources.APPLICATION_JSON;
import static sk.abstract_interface.Resources.CRYPTO_PRICE_BY_DATE_URL;
import static sk.abstract_interface.Resources.CURRENT_CRYPTO_PRICE_URL;

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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import sk.abstract_interface.Currency;
import sk.abstract_interface.MarketRequest;
import sk.abstract_interface.PriceType;
import sk.abstract_interface.URLResolver;

@Component
public final class CryptoMarketRequest implements MarketRequest {

	@Autowired
	private URLResolver urlResolver;

	@Autowired
	@Qualifier("accountCurrency")
	private Currency accountCurrency;

	@Autowired
	@Qualifier("tradingCurrency")
	private Currency tradingCurrency;

	@Autowired
	private HttpClient client;

	@Autowired
	private Gson gson;

	@Value("${polygon.api_key}")
	private String polygonApiKey;

	@Override
	@SuppressWarnings("unchecked")
	public double getCurrentPrice() throws IOException {
		String url = urlResolver.resolveParams(CURRENT_CRYPTO_PRICE_URL, tradingCurrency.getName());
		String jsonBody = getJson(url);

		Map<String, Object> responseMap = gson.fromJson(jsonBody, Map.class);
		Map<String, Object> marketData = (Map<String, Object>) responseMap.get("market_data");
		Map<String, Object> currentPrices = (Map<String, Object>) marketData.get("current_price");

		String currencyAcronym = getPolygonSupportedCurrencyAcronym().toLowerCase();
		String currentPrice = currentPrices.get(currencyAcronym).toString();

		return Double.valueOf(currentPrice);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<PriceType, Double> getPricesByDate(LocalDate date) throws IOException {
		// TODO: pick better names
		String tcAcronym = tradingCurrency.getAcronym();
//		String acAcronym = accountCurrency.getAcronym();
		String acAcronym = getPolygonSupportedCurrencyAcronym();

		String url = urlResolver.resolveParams(CRYPTO_PRICE_BY_DATE_URL, tcAcronym, acAcronym, date, date, polygonApiKey);
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

	// TODO: find better API than tradingview/polygon to remove this method
	/**
	 * Temporarily tightly coupled due to inability of polygon api to
	 * provide any other crypto market prices in EUR except Bitcoin.
	 * 
	 * In such case, all needs to be set to dollar in order to compute
	 * the correct market price percentage..
	 */
	private String getPolygonSupportedCurrencyAcronym() {
		if (tradingCurrency.equals(Currency.BITCOIN)) {
			return accountCurrency.getAcronym();
		} else {
			return Currency.DOLLAR.getAcronym();
		}
	}
}
