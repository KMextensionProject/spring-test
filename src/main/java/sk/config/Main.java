package sk.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import sk.abstract_interface.ExchangeAccount;
import sk.abstract_interface.Market;
import sk.implementation.BitcoinMarket;
import sk.implementation.CoinbaseAccount;

public class Main {

	public static void main(String[] args) throws Exception {	

		ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

		Market market = context.getBean(BitcoinMarket.class);
		market.updateState();

		System.out.println("Current bitcoin price: " + market.getCurrentPrice());
		System.out.println("Bitcoin first day of week opening price: " + market.getWeekOpeningPrice());
		System.out.println("Bitcoin first day of week closing price: " + market.getWeekClosingPrice());
		System.out.println("Bitcoin first day of month opening price: " + market.getMonthOpeningPrice());
		System.out.println("Bitcoin first day of month closing price: " + market.getMonthClosingPrice());

		ExchangeAccount exchange = context.getBean(CoinbaseAccount.class);
		exchange.updateState();

		System.out.println("Coinbase account id: " + exchange.getAccountId());
		System.out.println("Coinbase main currency: " + exchange.getCurrency());
		System.out.println("Coinbase account balance: " + exchange.getBalance() + exchange.getCurrency().getAcronym());

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
