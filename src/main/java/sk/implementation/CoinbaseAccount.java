package sk.implementation;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sk.abstract_interface.AccountCache;
import sk.abstract_interface.Currency;
import sk.abstract_interface.ExchangeAccount;

@Component
public class CoinbaseAccount extends ExchangeAccount {

	@Autowired
	private CoinbaseRequest accountRequest;

	@Autowired
	private AccountCache accountCache;

	@Override
	public void placeSellOrder(Currency currency, double amount) {
		throw new IllegalStateException();
	}

	@Override
	public void placeBuyOrder(Currency currency, double amount) {
		// TODO Auto-generated method stub
	}

	@Override
	public void updateState() throws Exception {
		balance = accountRequest.getAccountBalance(accountId);
	}

	@PostConstruct
	private void initAccountId() throws Exception {
		accountId = accountCache.getAccountIdByCurrency(currency);
	}
}
