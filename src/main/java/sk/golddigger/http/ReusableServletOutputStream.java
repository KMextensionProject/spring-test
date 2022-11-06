package sk.golddigger.http;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

public class ReusableServletOutputStream extends ServletOutputStream {

	private OutputStream outputStream;

	@Override
	public boolean isReady() {
		return false;
	}

	@Override
	public void setWriteListener(WriteListener writeListener) {
		
	}

	@Override
	public void write(int b) throws IOException {
	
	}

}
