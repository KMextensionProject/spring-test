package sk.abstract_interface;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class AccountCache {

	@Autowired
	private ExchangeRequest exchangeRequest;

	// called from multiple locations makes the request call..why?
	@Cacheable(cacheNames = {"accounts"}, sync = true)
	public List<Map<String, Object>> getAllAccounts() throws Exception {
		return exchangeRequest.getAllAccounts();
	}

}
