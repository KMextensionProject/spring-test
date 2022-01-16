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
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import sk.golddigger.exceptions.ApplicationFailure;
/**
 * This is the top level HTTP request class providing fundamental and
 * conventional HTTP request methods not specific to any business model.
 * 
 * @author mkrajcovic
 */
public abstract class DefaultHttpRequest {

	private static final Logger logger = Logger.getLogger(DefaultHttpRequest.class);

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
	protected String getJson(String url, List<Header> headers) {
		HttpGet request = new HttpGet(url);
		request.addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON);

		if (headers != null && !headers.isEmpty()) {
			headers.forEach(request::addHeader);
		}

		return getJson(request);
	}

	private String getJson(HttpRequestBase request) {
		try {
			HttpResponse response = client.execute(request);
			HttpEntity body = response.getEntity();

			// copyToString() of StreamUtils leaves the stream open when done with copying
			return EntityUtils.toString(body, UTF_8);
		} catch (IOException error) {
			logger.error("Error: ", error);
			throw new ApplicationFailure("httpRequestError", error);
		}
	}

	protected void logPayload(String payload) {
		if (logger.isDebugEnabled()) {
			logger.debug(payload);
		}
	}
}
