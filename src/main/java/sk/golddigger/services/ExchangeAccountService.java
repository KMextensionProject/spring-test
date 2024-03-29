package sk.golddigger.services;

import static sk.golddigger.enums.Resources.FILLED_ORDERS_TEMPLATE;
import static sk.golddigger.utils.MessageResolver.resolveMessage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import sk.golddigger.cache.AccountCache;
import sk.golddigger.core.ExchangeAccount;
import sk.golddigger.core.ExchangeRequest;
import sk.golddigger.core.RequestDateTime;
import sk.golddigger.enums.Currency;
import sk.golddigger.exceptions.ClientSideFailure;
import sk.golddigger.utils.MapUtils;
import sk.golddigger.utils.TypeUtils;
import sk.golddigger.utils.XlsxUtils;

@Service
public class ExchangeAccountService {

	private static final Logger logger = Logger.getLogger(ExchangeAccountService.class);

	@Autowired
	private ExchangeAccount exchangeAccount;

	@Autowired
	private ExchangeRequest exchangeRequest;

	@Autowired
	private AccountCache accountCache;

	@Autowired
	private RequestDateTime requestTime;

	public Map<String, Object> getAccountComplexOverview() {
		Map<String, Object> accountComplexOverview = new LinkedHashMap<>(2);
		accountComplexOverview.put("default_account", getDefaultAccountInfo());
		accountComplexOverview.put("trading_account", getTradingAccountInfo());

		return accountComplexOverview;
	}

	private Map<String, Object> getDefaultAccountInfo() {
		Currency accountCurrency = exchangeAccount.getAccountCurrency();

		Object bestBuyOrderRate = TypeUtils.getValueByCondition(e -> e > 0, exchangeAccount.getBestOrderBuyRate(), "No order has been placed in this year.");

		Map<String, Object> defaultAccountInfo = new LinkedHashMap<>();
		defaultAccountInfo.put("account_id", exchangeAccount.getAccountId());
		defaultAccountInfo.put("currency", accountCurrency.getName());
		defaultAccountInfo.put("currency_acronym", accountCurrency.getAcronym());
		defaultAccountInfo.put("balance", exchangeAccount.getBalance());
		defaultAccountInfo.put("best_buy_order_rate", bestBuyOrderRate);

		return defaultAccountInfo;
	}

	private Map<String, Object> getTradingAccountInfo() {
		Currency tradingCurrency = exchangeAccount.getTradingCurrency();

		String tradingAccountId = accountCache.getAccountIdByCurrency(tradingCurrency);
		double tradingAccountBalance = exchangeRequest.getAccountBalance(tradingAccountId);

		Map<String, Object> tradingAccountInfo = new LinkedHashMap<>();
		tradingAccountInfo.put("account_id", tradingAccountId);
		tradingAccountInfo.put("currency", tradingCurrency.getName());
		tradingAccountInfo.put("currency_acronym", tradingCurrency.getAcronym());
		tradingAccountInfo.put("balance", tradingAccountBalance);

		return tradingAccountInfo;
	}

	/**
	 * Generates this year's filled orders report to excel document.
	 * Including buy orders and sell orders.
	 */
	public void generateOrdersReportToExcel(Integer year, HttpServletResponse response) {
		LocalDate today = requestTime.getLocalDateUTC();
		validateYearRange(year, 2000, today.getYear(), response);

		String fileName = "Filled_orders_" + today + ".xlsx";
		List<Map<String, Object>> filledOrders = getOrdersFilledInYear(year);
		Map<String, Object> dataSource = new HashMap<>();
		dataSource.put("table", filledOrders);

		try {
			XlsxUtils.generateXlsx(FILLED_ORDERS_TEMPLATE, dataSource, fileName, response);
		} catch (IOException | InvalidFormatException e) {
			logger.error("Error: ", e);
		}
	}

	private void validateYearRange(Integer year, int from, int to, HttpServletResponse response) {
		if (year == null) {
			return;
		} else if (year < from || year > to) {
			String message = resolveMessage("yearValidation", from, to);
			logger.warn(message);
			throw new ClientSideFailure(message);
		}
	}

	private List<Map<String, Object>> getOrdersFilledInYear(Integer year) {
		Stream<Map<String, Object>> stream = exchangeRequest.getAllOrderFills().stream();
		if (year != null) {
			stream = stream.filter(e -> filterByYear(e, year));
		}
		return stream.map(this::adjustPropertiesForExcel)
			.collect(Collectors.toList());
	}

	private boolean filterByYear(Map<String, Object> map, int year) {
		return ZonedDateTime.parse(String.valueOf(map.get("created_at"))).getYear() == year;
	}

	private Map<String, Object> adjustPropertiesForExcel(Map<String, Object> data) {
		// remove time from date because of excel template formating
		XlsxUtils.removeTimeFromDate(data, "created_at");

		// size is reserved keyword in JEXL
		MapUtils.renameKey(data, "size", "order_size");
		updateMapPropertyAsDouble(data, "order_size");

		// to properly round these by excel template, they must be of type Number
		updateMapPropertyAsDouble(data, "price");
		updateMapPropertyAsDouble(data, "fee");
		updateMapPropertyAsDouble(data, "usd_volume");

		return data;
	}

	private void updateMapPropertyAsDouble(Map<String, Object> map, String propertyName) {
		String stringValue = String.valueOf(map.get(propertyName));
		Double value = Double.parseDouble(stringValue);
		map.put(propertyName, value);
	}
}
