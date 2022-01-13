package sk.golddigger.config;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class HttpClientConfig {

	private static final Logger logger = Logger.getLogger(HttpClientConfig.class);

	private static final String LOCALHOST_URL = "http://localhost";
	private static final int LOCALHOST_PORT = 8080;

	private static final int MAX_LOCALHOST_CONNECTIONS = 3;
	private static final int MAX_ROUTE_CONNECTIONS = 3;
	private static final int MAX_TOTAL_CONNECTIONS = 10;

	@Bean
	public HttpClient getHttpClient() {
		return HttpClients.custom()
				.setConnectionManager(getPoolingHttpConnectionManager())
				.build();
	}

	@Bean
	public PoolingHttpClientConnectionManager getPoolingHttpConnectionManager() {
		PoolingHttpClientConnectionManager poolingHttpConnectionManager = new PoolingHttpClientConnectionManager();
		poolingHttpConnectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
		poolingHttpConnectionManager.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);

		HttpHost localhost = new HttpHost(LOCALHOST_URL, LOCALHOST_PORT);
		poolingHttpConnectionManager.setMaxPerRoute(new HttpRoute(localhost), MAX_LOCALHOST_CONNECTIONS);

		logConnectionPool();
		return poolingHttpConnectionManager;
	}

	private void logConnectionPool() {
		if (logger.isDebugEnabled()) {
			logger.debug(resolveMessage("httpConnectionPool",
					MAX_LOCALHOST_CONNECTIONS,
					MAX_TOTAL_CONNECTIONS,
					MAX_ROUTE_CONNECTIONS));
		}
	}
}
