package sk.golddigger.services;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sk.golddigger.cache.AccountCache;
import sk.golddigger.core.ExchangeAccount;
import sk.golddigger.core.ExchangeRequest;
import sk.golddigger.enums.Currency;

@Service
public class ExchangeAccountService {

	@Autowired
	private ExchangeAccount exchangeAccount;

	@Autowired
	private ExchangeRequest exchangeRequest;

	@Autowired
	private AccountCache accountCache;

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
		// TODO: implement
	}

}
