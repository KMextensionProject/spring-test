package sk.config;

import java.util.Arrays;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import sk.abstract_interface.CacheNames;
import sk.abstract_interface.Currency;

// TODO: add custom exceptions
// TODO: cache message resolver

@Configuration
@EnableCaching
@EnableScheduling
@PropertySource("classpath:account_defaults.properties")
@ComponentScan(basePackages = "sk")
public class AppConfig {

	@Bean
	public HttpClient getHttpClient() {
		return HttpClientBuilder.create().build();
	}

	@Bean(name = {"accountCurrency"})
	public Currency loadDefaultAccountCurrency(@Value("${account.currency}") String currency) {
		System.out.println("injecting account.currency");
		return Currency.valueOf(currency);
	}

	@Bean(name = {"tradingCurrency"})
	public Currency loadTradingCurrency(@Value("${trading.currency}") String currency) {
		System.out.println("injecting trading.currency");
		return Currency.valueOf(currency);
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