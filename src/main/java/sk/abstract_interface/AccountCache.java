package sk.abstract_interface;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class AccountCache {

	@Autowired
	private ExchangeRequest exchangeRequest;

	@Cacheable(value = CacheNames.ACCOUNTS_CACHE, sync = true)
	public List<Map<String, Object>> getAllAccounts() throws Exception {
		return exchangeRequest.getAllAccounts();
	}

	@CacheEvict(value = CacheNames.ACCOUNTS_CACHE, allEntries = true, beforeInvocation = true)
	public void dropAccountCache() {
		// log
	}
}
