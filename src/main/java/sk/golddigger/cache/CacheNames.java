package sk.golddigger.cache;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

public class CacheNames {

	private CacheNames() {
		throw new IllegalStateException(resolveMessage("factoryClassInstantiationError", CacheNames.class));
	}

	public static final String ACCOUNTS_CACHE = "accounts";
	public static final String ACCOUNT_ID_CACHE = "accountIDs";
}
