package sk.golddigger.http;

import static sk.golddigger.enums.Resources.CRYPTO_PRICE_BY_DATE_URL;
import static sk.golddigger.enums.Resources.CURRENT_CRYPTO_PRICE_URL;
import static sk.golddigger.utils.MessageResolver.resolveMessage;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import sk.golddigger.core.MarketRequest;
import sk.golddigger.enums.Currency;
import sk.golddigger.enums.PriceType;
import sk.golddigger.utils.URLResolver;

/**
 * This class should be able to provide methods to specific market data 
 * related to the configured trading currency.
 * <p>Internally it uses coingecko and polygon REST API to retrieve crypto market prices. This requires
 * the polygon API key to be specified inside this application's configuration properties file.
 * One should be aware of limits that come with the default token allowing max 5 HTTP requests per minute.</p>
 * 
 * @author mkrajcovic
 */
@Component
public final class CryptoMarketRequest extends DefaultHttpRequest implements MarketRequest {

	private static final Logger logger = Logger.getLogger(CryptoMarketRequest.class);

	@Autowired
	private URLResolver urlResolver;

	@Autowired
	@Qualifier("accountCurrency")
	private Currency accountCurrency;

	@Autowired
	@Qualifier("tradingCurrency")
	private Currency tradingCurrency;

	@Autowired
	private Gson gson;

	@Value("${POLYGON_API_KEY:null}")
	private String polygonApiKey;

	/**
	 * Retrieves the current market price of the configured trading currency.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public double getCurrentPrice() {
		Map<String, Object> marketData = getGeckoMarketData();
		Map<String, Object> currentPrices = (Map<String, Object>) marketData.get("current_price");

		String currencyAcronym = getPolygonSupportedCurrencyAcronym().toLowerCase();
		String currentPrice = currentPrices.get(currencyAcronym).toString();

		return Double.valueOf(currentPrice);
	}

	@Override
	@SuppressWarnings("unchecked")
	public double getAllTimeHigh() {
		String currency = accountCurrency.getAcronym().toLowerCase();
		Map<String, Object> marketData = getGeckoMarketData();
		Map<String, Object> ath = (Map<String, Object>) marketData.get("ath");

		return Double.valueOf(ath.get(currency).toString());
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getGeckoMarketData() {
		String url = urlResolver.resolveParams(CURRENT_CRYPTO_PRICE_URL, tradingCurrency.getName());
		String jsonBody = getJson(url, null);
		logPayload(jsonBody);

		Map<String, Object> responseMap = gson.fromJson(jsonBody, Map.class);
		return (Map<String, Object>) responseMap.get("market_data");
	}

	/**
	 * Retrieves the opening, closing, highest, lowest prices of the configured 
	 * trading currency for the specified date.
	 * <p>This method will not return any results if called with the current date,
	 * by polygon API documentation, it will block until the values are present.
	 * Logically all the data can not be present yet, and so the getCurrentPrice()
	 * should be used instead.</p>
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<PriceType, Double> getPricesByDate(LocalDate date) {
		String tcAcronym = tradingCurrency.getAcronym();
		String acAcronym = getPolygonSupportedCurrencyAcronym();

		String url = urlResolver.resolveParams(CRYPTO_PRICE_BY_DATE_URL, tcAcronym, acAcronym, date, date, polygonApiKey);
		String jsonBody = getJson(url, null);
		logPayload(jsonBody);

		Map<String, Object> topLevelObject = gson.fromJson(jsonBody, Map.class);
		List<Map<String, Object>> data = (List<Map<String, Object>>) topLevelObject.get("results");

		Map<PriceType, Double> result = new EnumMap<>(PriceType.class);

		for (PriceType type : PriceType.values()) {
			String price = String.valueOf(data.get(0).get(type.getMark()));
			result.put(type, Double.valueOf(price));
		}

		return result;
	}

	// TODO: find better API than tradingview/polygon to remove this method
	/**
	 * Temporarily tightly coupled due to inability of polygon API to
	 * provide any other crypto market prices in EUR except Bitcoin.
	 * 
	 * In such case, all need to be set to USD in order to compute
	 * the correct market price percentage..
	 */
	private String getPolygonSupportedCurrencyAcronym() {
		if (tradingCurrency.equals(Currency.BITCOIN)) {
			return accountCurrency.getAcronym();
		} else {
			return Currency.DOLLAR.getAcronym();
		}
	}

	// This validates polygon api key only whether it is present, 
	// and not whether it is a valid api key !
	@PostConstruct
	private void validatePolygonApiKeyPresence() {
		if ("null".equals(polygonApiKey)) {
			logger.error(resolveMessage("missingPolygonApiKey"));
			System.exit(1);
		}
		logger.info(resolveMessage("polygonApiKeyValidation"));
	}
}
