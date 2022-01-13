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
/**
 * This is the top level HTTP request class providing fundamental and
 * conventional HTTP request methods not specific to any business model.
 * 
 * @author mkrajcovic
 */
public abstract class DefaultHttpRequest {

	private HttpClient client;

	@Autowired
	public final void setHttpClient(HttpClient client) {
		this.client = client;
	}

	/**
	 * Calls the HTTP GET request on the specified URL.
	 * @param url
	 * @param headers - list of headers required for request, may be null.
	 * @return JSON response as pretty-printed string
	 * @throws ClientProtocolException in case of an HTTP protocol error
	 * @throws IOException if there is some problem with reading InputStream from response
	 */
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
