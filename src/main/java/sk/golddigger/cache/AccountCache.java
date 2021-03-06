package sk.golddigger.cache;
import static sk.golddigger.utils.MessageResolver.resolveMessage;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import sk.golddigger.core.ExchangeRequest;
import sk.golddigger.enums.Currency;

/**
 * This class represents a basic map based cache for storing all available account
 * metadata and all account identifiers based on its currency.
 * These are needed in order to perform quick account data lookup that are required
 * by exchange APIs.
 * <p> Internally, every map is created with the initial capacity of 256 elements. </p>
 * 
 * @author mkrajcovic
 */
@Component
public class AccountCache {

	private static final Logger logger = Logger.getLogger(AccountCache.class);

	@Autowired
	private ExchangeRequest exchangeRequest;

	@Cacheable(value = CacheNames.ACCOUNTS_CACHE, sync = true)
	public List<Map<String, Object>> getAllAccounts() {
		return exchangeRequest.getAllAccounts();
	}

	@Cacheable(value = CacheNames.ACCOUNT_ID_CACHE, sync = true)
	public String getAccountIdByCurrency(Currency currency) {
		List<Map<String, Object>> accounts = getAllAccounts();

		Optional<String> accountId = accounts.stream()
			.filter(account -> hasRequiredCurrency(account, currency))
			.map(this::getAccountId)
			.findAny();

		if (!accountId.isPresent()) {
			logger.error(resolveMessage("accountNotFound", currency));
			System.exit(1);
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
		logger.info("ACCOUNTS_CACHE has been dropped");
	}

	@CacheEvict(value = CacheNames.ACCOUNT_ID_CACHE, allEntries = true, beforeInvocation = true)
	public void dropAccountIDCache() {
		logger.info("ACCOUNT_ID_CACHE has been dropped");
	}
}
