package sk.implementation;

import java.util.List;
import java.util.Map;

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

	// TODO: cache account ids
	// TODO: use optional with assignment and validate content / use custom exception
	// TODO: cached method is called multiple times when called from different locations
	@PostConstruct
	private void initAccountId() throws Exception {
		List<Map<String, Object>> accounts = accountCache.getAllAccounts();
		this.accountId = accounts.stream()
			.filter(this::containsCurrency)
			.map(this::getID)
			.findFirst().get();
	}

	private boolean containsCurrency(Map<String, Object> account) {
		return String.valueOf(account.get("currency")).equals(currency.getAcronym());
	}

	private String getID(Map<String, Object> account) {
		return String.valueOf(account.get("id"));
	}
}
