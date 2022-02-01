package sk.golddigger.config;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import sk.golddigger.cache.CacheNames;
import sk.golddigger.enums.Currency;

/**
 * This is the base configuration class.
 * Usually provides beans that are required among multiple component classes.
 * If the bean injection depends on the user input, mostly via account settings
 * properties file, then it also does the validation on them. If there is no
 * such property that is required by multiple components, than the component
 * class itself is responsible for its property injection and validation.
 * 
 * @author mkrajcovic
 */
@Configuration
@EnableCaching
@EnableWebMvc
@PropertySource({"classpath:account_defaults.properties", "classpath:email_provider.properties"})
@ComponentScan(basePackages = "sk")
public class AppConfig {

	private static final Logger logger = Logger.getLogger(AppConfig.class);

	@Bean(name = {"accountCurrency"})
	public Currency loadDefaultAccountCurrency(@Value("${account.currency:null}") String accountCurrency) {
		Currency currency = null;
		try {
			currency = Currency.valueOf(accountCurrency.toUpperCase());
			logger.info(resolveMessage("accountCurrencySet", currency));
		} catch (Exception error) {
			logCurrencyFault("emptyAccountCurrency", accountCurrency);
		}
		return currency;
	}

	@Bean(name = {"tradingCurrency"})
	public Currency loadTradingCurrency(@Value("${trading.currency:null}") String tradingCurrency) {
		Currency currency = null;
		try {
			currency = Currency.valueOf(tradingCurrency.toUpperCase());
			logger.info(resolveMessage("tradingCurrencySet", tradingCurrency));
		} catch (Exception error) {
			logCurrencyFault("emptyTradingCurrency", tradingCurrency);
		}
		return currency;
	}

	private void logCurrencyFault(String messageCode, String currency) {
		if (currency.equals("null") || currency.isEmpty()) {
			logger.error(resolveMessage(messageCode));
		} else {
			logger.error(resolveMessage("notSupportedCurrency", currency));
		}
	}

	@Bean
	public Gson getPrettyPrintingGson() {
		return new GsonBuilder()
				.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
				.setPrettyPrinting()
				.create();
	}

	@Bean
	public CacheManager cacheManager() {
		SimpleCacheManager cacheManager = new SimpleCacheManager();
		cacheManager.setCaches(Arrays.asList(
				new ConcurrentMapCache(CacheNames.ACCOUNTS_CACHE),
				new ConcurrentMapCache(CacheNames.ACCOUNT_ID_CACHE)));

		return cacheManager;
	}
}