package sk.golddigger.config;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import sk.golddigger.cache.AccountCache;
import sk.golddigger.core.BuyPredicate;
import sk.golddigger.core.CoinbaseAccount;
import sk.golddigger.core.CryptoMarket;
import sk.golddigger.core.ExchangeAccount;
import sk.golddigger.core.ExchangeRequest;
import sk.golddigger.core.Market;
import sk.golddigger.core.MarketPredicate;
import sk.golddigger.http.CoinbaseRequest;

public class Main {

	private static final Logger logger = Logger.getLogger(Main.class);

	public static void main(String[] args) throws Exception {	

		ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

		AccountCache accountCache = context.getBean(AccountCache.class);
		ExchangeRequest request = context.getBean(CoinbaseRequest.class);

		Market market = context.getBean(CryptoMarket.class);
		market.updateState();

		ExchangeAccount exchange = context.getBean(CoinbaseAccount.class);
		exchange.updateState();

		MarketPredicate buyPredicate = context.getBean(BuyPredicate.class);

		String tradingCurrencyName = exchange.getTradingCurrency().getName();
		
		logger.info("-");
		logger.info("current " + tradingCurrencyName + " price: " + market.getCurrentPrice() + exchange.getAccountCurrency().getAcronym());
		logger.info(tradingCurrencyName + " first day of week opening price: " + market.getFirstDayOfWeekOpeningPrice() + getAccountCurrencyAcronym(exchange));
		logger.info(tradingCurrencyName + " first day of week closing price: " + market.getFirstDayOfWeekClosingPrice() + getAccountCurrencyAcronym(exchange));
		logger.info(tradingCurrencyName + " first day of month opening price: " + market.getFirstDayOfMonthOpeningPrice() + getAccountCurrencyAcronym(exchange));
		logger.info(tradingCurrencyName + " first day of month closing price: " + market.getFirstDayOfMonthClosingPrice() + getAccountCurrencyAcronym(exchange));
		logger.info(tradingCurrencyName + " first day of year opening price: " + market.getFirstDayOfYearOpeningPrice() + getAccountCurrencyAcronym(exchange));
		logger.info(tradingCurrencyName + " all time high price: " + market.getAllTimeHigh() + getAccountCurrencyAcronym(exchange));
		logger.info("-");

		logger.info("Coinbase account id: " + exchange.getAccountId());
		logger.info("Coinbase currency: " + exchange.getAccountCurrency());
		logger.info("Coinbase account balance: " + exchange.getBalance() + getAccountCurrencyAcronym(exchange));
		logger.info("-");

		String tradingAccountId = accountCache.getAccountIdByCurrency(exchange.getTradingCurrency());
		logger.info("Trading account id: " + tradingAccountId);
		logger.info("Trading currency: " + exchange.getTradingCurrency());
		logger.info("Trading account balance: " + request.getAccountBalance(tradingAccountId) + getTradingCurrencyAcronym(exchange));
		logger.info("The best filled buy order rate: " + exchange.getBestOrderBuyRate() + getAccountCurrencyAcronym(exchange));
		logger.info("-");

		logger.info("Is market state suitable for buy request: " + buyPredicate.testMarket(market));
		
		// close it!
		((AnnotationConfigApplicationContext)context).close();
	}

	private static String getTradingCurrencyAcronym(ExchangeAccount account) {
		return account.getTradingCurrency().getAcronym();
	}

	private static String getAccountCurrencyAcronym(ExchangeAccount account) {
		return account.getAccountCurrency().getAcronym();
	}

}
