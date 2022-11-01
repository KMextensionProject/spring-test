package sk.golddigger.http;

import javax.servlet.ServletInputStream;
import java.io.InputStream;
import javax.servlet.ReadListener;
import java.io.IOException;

public class ReusableServletInputStream extends ServletInputStream {
	private InputStream inputStream;

	private ServletInputStream servletInputStream = new ServletInputStream() {
		boolean isFinished = false;
		boolean isReady = true;
		@SuppressWarnings("unused")
		ReadListener readListener;

		@Override
		public int read() throws IOException {
			int i = inputStream.read();
			isFinished = i == -1;
			isReady = !isFinished;
			return i;
		}

		@Override
		public boolean isFinished() {
			return isFinished;
		}

		@Override
		public boolean isReady() {
			return isReady;
		}

		@Override
		public void setReadListener(ReadListener readListener) {
			this.readListener = readListener;
		}
	};

	public void setInputStream(InputStream is) {
		this.inputStream = is;
	}

	@Override
	public int available() throws IOException {
		return inputStream.available();
	}

	@Override
	public void close() throws IOException {
		inputStream.close();
	}

	@Override
	public void mark(int readLimit) {
		inputStream.mark(readLimit);
	}

	@Override
	public boolean markSupported() {
		return inputStream.markSupported();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return inputStream.read(b, off, len);
	}

	public int readline(byte[] b, int off, int len) throws IOException {
		if (len <= 0) {
			return 0;
		}
		int count = 0, c;
		while ((c = read()) != -1) {
			b[off++] = (byte) c;
			count++;
			if (c == '\n' || count == len) {
				break;
			}
		}
		return count > 0 ? count : -1;
	}

	@Override
	public void reset() throws IOException {
		inputStream.reset();
	}

	@Override
	public long skip(long n) throws IOException {
		return inputStream.skip(n);
	}

	@Override
	public int read() throws IOException {
		return inputStream.read();
	}

	@Override
	public void setReadListener(ReadListener readListener) {
		servletInputStream.setReadListener(readListener);
	}

	@Override
	public boolean isReady() {
		return servletInputStream.isReady();
	}

	@Override
	public boolean isFinished() {
		return servletInputStream.isFinished();
	}
}