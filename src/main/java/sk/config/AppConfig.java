package sk.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import sk.abstract_interface.CacheNames;
import sk.abstract_interface.Currency;

// TODO: add custom exceptions
// TODO: cache message resolver

@Configuration
@EnableCaching
@PropertySource("classpath:account_defaults.properties")
@ComponentScan(basePackages = "sk")
public class AppConfig {

	// TODO: specify default values and proper validation
	@Bean(name = {"accountCurrency"})
	public Currency loadDefaultAccountCurrency(@Value("${account.currency}") String currency) {
		return Currency.valueOf(currency.toUpperCase());
	}

	@Bean(name = {"tradingCurrency"})
	public Currency loadTradingCurrency(@Value("${trading.currency}") String currency) {
		return Currency.valueOf(currency.toUpperCase());
	}

	@Bean
	public Gson getPrettyPrintingGson() {
		return new GsonBuilder().setPrettyPrinting().create();
	}

	@Bean
	public CacheManager cacheManager() {
		SimpleCacheManager cacheManager = new SimpleCacheManager();
		cacheManager.setCaches(Arrays.asList(
				new ConcurrentMapCache(CacheNames.ACCOUNTS_CACHE),
				new ConcurrentMapCache(CacheNames.ACCOUNT_ID_CACHE),
				new ConcurrentMapCache(CacheNames.MESSAGES_CACHE)));
		return cacheManager;
	}
}