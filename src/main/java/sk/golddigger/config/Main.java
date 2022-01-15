package sk.golddigger.config;

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

		System.out.println("current " + tradingCurrencyName + " price: " + market.getCurrentPrice() + exchange.getAccountCurrency().getAcronym());
		System.out.println(tradingCurrencyName + " first day of week opening price: " + market.getFirstDayOfWeekOpeningPrice() + getAccountCurrencyAcronym(exchange));
		System.out.println(tradingCurrencyName + " first day of week closing price: " + market.getFirstDayOfWeekClosingPrice() + getAccountCurrencyAcronym(exchange));
		System.out.println(tradingCurrencyName + " first day of month opening price: " + market.getFirstDayOfMonthOpeningPrice() + getAccountCurrencyAcronym(exchange));
		System.out.println(tradingCurrencyName + " first day of month closing price: " + market.getFirstDayOfMonthClosingPrice() + getAccountCurrencyAcronym(exchange));
		System.out.println(tradingCurrencyName + " first day of year opening price: " + market.getFirstDayOfYearOpeningPrice() + getAccountCurrencyAcronym(exchange));
		System.out.println();

		System.out.println("Coinbase account id: " + exchange.getAccountId());
		System.out.println("Coinbase currency: " + exchange.getAccountCurrency());
		System.out.println("Coinbase account balance: " + exchange.getBalance() + getAccountCurrencyAcronym(exchange));
		System.out.println();

		String tradingAccountId = accountCache.getAccountIdByCurrency(exchange.getTradingCurrency());
		System.out.println("Trading account id: " + tradingAccountId);
		System.out.println("Trading currency: " + exchange.getTradingCurrency());
		System.out.println("Trading account balance: " + request.getAccountBalance(tradingAccountId) + getTradingCurrencyAcronym(exchange));
		System.out.println("The best filled buy order rate: " + exchange.getBestOrderBuyRate() + getAccountCurrencyAcronym(exchange));
		System.out.println();

		System.out.println("Is market state suitable for buy request: " + buyPredicate.testMarket(market));
		
		// close it!
		((AnnotationConfigApplicationContext)context).close();
	}

	private static String getTradingCurrencyAcronym(ExchangeAccount account) {
		return account.getTradingCurrency().getAcronym();
	}

	private static String getAccountCurrencyAcronym(ExchangeAccount account) {
		return account.getAccountCurrency().getAcronym();
	}

	public static void sleep(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
