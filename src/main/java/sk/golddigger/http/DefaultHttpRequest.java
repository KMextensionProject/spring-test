package sk.golddigger.http;

import static java.nio.charset.StandardCharsets.UTF_8;
import static sk.golddigger.enums.Resources.APPLICATION_JSON;

import java.io.IOException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class DefaultHttpRequest {

	private HttpClient client;

	@Autowired
	public final void setHttpClient(HttpClient client) {
		this.client = client;
	}

	protected String getJson(String url, List<Header> headers) throws ClientProtocolException, IOException {
		HttpGet request = new HttpGet(url);
		request.addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON);

		if (headers != null && !headers.isEmpty()) {
			headers.forEach(request::addHeader);
		}

		HttpResponse response = client.execute(request);
		HttpEntity body = response.getEntity();

		// leaves the stream open when done with copying
//		String jsonBody = StreamUtils.copyToString(body.getContent(), StandardCharsets.UTF_8);
		return EntityUtils.toString(body, UTF_8);
	}

}
