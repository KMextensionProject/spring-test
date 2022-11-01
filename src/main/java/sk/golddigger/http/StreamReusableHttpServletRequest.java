package sk.golddigger.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.poi.util.IOUtils;

public class StreamReusableHttpServletRequest extends HttpServletRequestWrapper {

	private byte[] rawData = {};
	private HttpServletRequest request;
	private ReusableServletInputStream servletStream;

	public StreamReusableHttpServletRequest(HttpServletRequest request) {
		super(request);
		this.request = request;
		this.servletStream = new ReusableServletInputStream();
	}

	private void initRawData() throws IOException {
		if (rawData.length == 0) {
			byte[] b = IOUtils.toByteArray(this.request.getInputStream());
			if (b != null)
				rawData = b;
		}
		servletStream.setInputStream(new ByteArrayInputStream(rawData));
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		initRawData();
		return servletStream;
	}

	@Override
	public BufferedReader getReader() throws IOException {
		initRawData();
		String encoding = getCharacterEncoding();
		if (encoding != null) {
			return new BufferedReader(new InputStreamReader(servletStream, encoding));
		} else {
			return new BufferedReader(new InputStreamReader(servletStream));
		}
	}

	public byte[] getRawData() throws IOException {
		if (this.rawData.length == 0) {
			initRawData();
		}
		return this.rawData;
	}
}
