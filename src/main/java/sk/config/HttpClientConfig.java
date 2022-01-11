package sk.config;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class HttpClientConfig {

	/*
	 * TODO: extract the literal values to a separate enum
	 * TODO: pick the most suitable connection limits
	 */
	@Bean
	public PoolingHttpClientConnectionManager getPoolingHttpConnectionManager() {
		PoolingHttpClientConnectionManager poolingHttpConnectionManager = new PoolingHttpClientConnectionManager();
		poolingHttpConnectionManager.setMaxTotal(20);
		poolingHttpConnectionManager.setDefaultMaxPerRoute(3);

		HttpHost localhost = new HttpHost("http://localhost", 8080);
		poolingHttpConnectionManager.setMaxPerRoute(new HttpRoute(localhost), 30);
		return poolingHttpConnectionManager;
	}

	@Bean
	public HttpClient getHttpClient() {
		return HttpClients.custom()
				.setConnectionManager(getPoolingHttpConnectionManager())
				.build();
	}

}
