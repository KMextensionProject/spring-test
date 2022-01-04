package sk.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import sk.abstract_interface.AccountCache;
import sk.abstract_interface.Currency;
import sk.abstract_interface.ExchangeAccount;
import sk.abstract_interface.ExchangeRequest;
import sk.abstract_interface.Market;
import sk.implementation.BitcoinMarket;
import sk.implementation.CoinbaseAccount;
import sk.implementation.CoinbaseRequest;

public class Main {

	public static void main(String[] args) throws Exception {	

		ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

		Market market = context.getBean(BitcoinMarket.class);
		ExchangeAccount exchange = context.getBean(CoinbaseAccount.class);

		AccountCache accountCache = context.getBean(AccountCache.class);
		ExchangeRequest request = context.getBean(CoinbaseRequest.class);

		market.updateState();

		System.out.println("Current bitcoin price: " + market.getCurrentPrice());
		System.out.println("Bitcoin first day of week opening price: " + market.getWeekOpeningPrice());
		System.out.println("Bitcoin first day of week closing price: " + market.getWeekClosingPrice());
		System.out.println("Bitcoin first day of month opening price: " + market.getMonthOpeningPrice());
		System.out.println("Bitcoin first day of month closing price: " + market.getMonthClosingPrice());
		System.out.println();

		exchange.updateState();

		System.out.println("Coinbase account id: " + exchange.getAccountId());
		System.out.println("Coinbase currency: " + exchange.getCurrency());
		System.out.println("Coinbase account balance: " + exchange.getBalance() + exchange.getCurrency().getAcronym());
		System.out.println();

		String bitcoinAccountId = accountCache.getAccountIdByCurrency(Currency.BITCOIN);
		System.out.println("Coinbase account id: " + bitcoinAccountId);
		System.out.println("Coinbase currency: " + Currency.BITCOIN);
		System.out.println("Coinbase account balance: " + request.getAccountBalance(bitcoinAccountId) + Currency.BITCOIN.getAcronym());
		System.out.println();

		// close it!
		((AnnotationConfigApplicationContext)context).close();
	}

	public static void sleep(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
