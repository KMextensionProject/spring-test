package sk.bl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import sk.abstract_interface.AccountCache;
import sk.abstract_interface.Currency;
import sk.abstract_interface.ExchangeAccount;
import sk.abstract_interface.ExchangeRequest;
import sk.abstract_interface.Market;

@Component
public class ScheduledService {

	@Autowired
	private Market market;

	@Autowired
	private ExchangeAccount account;

	@Autowired
	private AccountCache accountCache;

	@Autowired
	private ExchangeRequest exchangeRequest;

	@Scheduled(initialDelay = 10_000, fixedRate = 360_000)
	public void action() throws Exception {
		System.out.println("scheduled task");

		// update account's state = balance and so on
		account.updateState();

		if (account.getBalance() > 1) {
			// get live market data
			market.updateState();

			// calculate percentage from these..
			market.getCurrentPrice();

			market.getWeekOpeningPrice();
			market.getMonthOpeningPrice();
			market.getYearOpeningPrice();
			market.getAllTimeHigh();

			// if we can pass one of the conditions..
			account.placeBuyOrder(Currency.BITCOIN, account.getBalance());

			// get account id from cache
			String btcAccountId = accountCache.getAccountIdByCurrency(Currency.BITCOIN);

			// get current account balance by id
			double btcAccountBalance = exchangeRequest.getAccountBalance(btcAccountId);
			System.out.println(btcAccountBalance);

			// TODO: add to account or somewhere the best buy of this year... it could be also retrieved by request..
			// that means.. also implement year border
		}
	}
}
