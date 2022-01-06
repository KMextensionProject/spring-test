package sk.abstract_interface;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import sk.exceptions.MissingAccount;

@Component
public class AccountCache {

	@Autowired
	private ExchangeRequest exchangeRequest;

	@Cacheable(value = CacheNames.ACCOUNTS_CACHE, sync = true)
	public List<Map<String, Object>> getAllAccounts() throws Exception {
		return exchangeRequest.getAllAccounts();
	}

	@Cacheable(value = CacheNames.ACCOUNT_ID_CACHE, sync = true)
	public String getAccountIdByCurrency(Currency currency) throws Exception {
		List<Map<String, Object>> accounts = getAllAccounts();

		Optional<String> accountId = accounts.stream()
			.filter(account -> hasRequiredCurrency(account, currency))
			.map(account -> getAccountId(account))
			.findAny();

		if (accountId.isEmpty()) {
			throw new MissingAccount("accountNotFound", currency);
		}

		return accountId.get();
	}

	private boolean hasRequiredCurrency(Map<String, Object> account, Currency currency) {
		return String.valueOf(account.get("currency")).equals(currency.getAcronym());
	}

	private String getAccountId(Map<String, Object> account) {
		return String.valueOf(account.get("id"));
	}

	@CacheEvict(value = CacheNames.ACCOUNTS_CACHE, allEntries = true, beforeInvocation = true)
	public void dropAccountCache() {
		// log
	}

	@CacheEvict(value = CacheNames.ACCOUNT_ID_CACHE, allEntries = true, beforeInvocation = true)
	public void dropAccountIDCache() {
		// log
	}
}