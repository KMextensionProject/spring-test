package sk.golddigger.services;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sk.golddigger.cache.AccountCache;
import sk.golddigger.config.EndpointLoader;

@Service
public class ApplicationService {

	@Autowired
	private EndpointLoader endpointLoader;

	@Autowired
	private AccountCache accountCache;

	public final Set<String> getAvailableEndpoints() {
		return endpointLoader.getApplicationEndpoints();
	}

	public void dropAccountCaches() {
		accountCache.dropAccountCache();
		accountCache.dropAccountIDCache();
	}
}
