package sk.golddigger.http;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class StreamReusableHttpServletResponse extends HttpServletResponseWrapper {

	private byte[] rawData = {};
	private HttpServletResponse response;
	private ReusableServletOutputStream servletStream;
	
	public StreamReusableHttpServletResponse(HttpServletResponse response) {
		super(response);
		this.response = response;
		this.servletStream = new ReusableServletOutputStream();
	}

	public byte[] getRawData() {
		return new byte[0];
	}

}
