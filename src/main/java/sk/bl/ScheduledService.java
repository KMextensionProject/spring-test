package sk.bl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import sk.abstract_interface.AccountCache;
import sk.abstract_interface.ExchangeAccount;
import sk.abstract_interface.ExchangeRequest;
import sk.abstract_interface.Market;

@Component // TODO: make this a service when the project type changes to webapp
public class ScheduledService {

	@Autowired
	private Market market;

	@Autowired
	private ExchangeAccount account;

	@Autowired
	private AccountCache accountCache;

	@Autowired
	private ExchangeRequest exchangeRequest;

	// TODO: validate the user input
	@Scheduled(initialDelayString = "${scheduler.initial_task_delay}", fixedRateString = "${scheduler.fixed_task_rate}")
	public void scheduledAction() throws Exception {
		account.updateState();

		if (account.getBalance() > 1) {
			market.updateState();

			if (market.isSuitableForBuyOrder()) {
				account.placeBuyOrder(account.getTradingCurrency(), account.getBalance());
				String tradingAccountId = accountCache.getAccountIdByCurrency(account.getTradingCurrency());

				// then retrieve the trading account state
				exchangeRequest.getAccountBalance(tradingAccountId);
			}
		}
	}
}
