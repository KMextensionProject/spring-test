package sk.golddigger.services;

import static sk.golddigger.enums.Resources.FILLED_ORDERS_TEMPLATE;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sk.golddigger.cache.AccountCache;
import sk.golddigger.core.ExchangeAccount;
import sk.golddigger.core.ExchangeRequest;
import sk.golddigger.core.RequestDateTime;
import sk.golddigger.enums.Currency;
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

		Map<String, Object> defaultAccountInfo = new LinkedHashMap<>();
		defaultAccountInfo.put("account_id", exchangeAccount.getAccountId());
		defaultAccountInfo.put("currency", accountCurrency.getName());
		defaultAccountInfo.put("currency_acronym", accountCurrency.getAcronym());
		defaultAccountInfo.put("balance", exchangeAccount.getBalance());
		defaultAccountInfo.put("best_buy_rate", exchangeAccount.getBestOrderBuyRate());

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
	public void generateOrdersReportToExcel(HttpServletResponse response) {
		LocalDate today = requestTime.getLocalDateUTC();
		String fileName = "Filled_orders_" + today + ".xlsx";

		List<Map<String, Object>> filledOrders = getOrdersFilledInYear(today.getYear());
		Map<String, Object> dataSource = new HashMap<>();
		dataSource.put("table", filledOrders);

		try {
			XlsxUtils.generateXlsx(FILLED_ORDERS_TEMPLATE, dataSource, fileName, response);
		} catch (IOException | InvalidFormatException e) {
			logger.error("Error: ", e);
		}
	}

	// TODO: size field is reserved by JEXL so it cannot be used -> re-map it
	// + set excel document to round/trim larger numbers
	// + sort data by date and remove time units
	private List<Map<String, Object>> getOrdersFilledInYear(int year) {
		List<Map<String, Object>> filledOrders = exchangeRequest.getAllOrderFills();
		filledOrders.removeIf(e -> ZonedDateTime.parse(String.valueOf(e.get("created_at"))).getYear() != year);
		return filledOrders;
	}

}
