package sk.config;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import sk.abstract_interface.Currency;

@Configuration
@PropertySource("classpath:account_defaults.properties")
@ComponentScan(basePackages = "sk")
public class AppConfig {

	@Bean
	public HttpClient getHttpClient() {
		return HttpClientBuilder.create().build();
	}

	@Bean
	public Currency loadDefaultAccountCurrency(@Value("${currency}") String currency) {
		return Currency.valueOf(currency);
	}
}