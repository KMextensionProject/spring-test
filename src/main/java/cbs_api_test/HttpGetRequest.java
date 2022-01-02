package cbs_api_test;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class HttpGetRequest {

	private HttpClient client;

	public HttpGetRequest() {
		client = HttpClientBuilder.create().build();
	}

	public String get(String url, String... headers) throws ClientProtocolException, IOException {
		validateHeaderParamLength(headers);

		HttpGet request = new HttpGet(url);
		addHeaders(request, headers);
		HttpResponse response = client.execute(request);
		HttpEntity body = response.getEntity();

		return EntityUtils.toString(body);
	}

	private void validateHeaderParamLength(String[] headers) {
		if (headers.length % 2 != 0) {
			throw new IllegalArgumentException("Unexpected data count. Header comes in key/value pairs");
		}
	}

	private void addHeaders(HttpRequest request, String[] headers) {
		if (headers.length > 0) {
			for (int i = 0; i < headers.length; i += 2) {
				request.addHeader(headers[i], headers[i + 1]);
			}
		}
	}

	@Override
	protected final void finalize() throws Exception {
		((CloseableHttpClient)client).close();
	}
}

