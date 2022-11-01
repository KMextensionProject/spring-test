package sk.golddigger.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import org.apache.commons.io.IOUtils;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import javax.servlet.ServletInputStream;

// change this name, because i am not resetting this stream, but i am reusing it 
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
