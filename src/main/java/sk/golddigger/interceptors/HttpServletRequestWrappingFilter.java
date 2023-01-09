package sk.golddigger.interceptors;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import sk.golddigger.http.StreamReusableHttpServletRequest;

/**
 *
 * @author martin
 */
@WebFilter(urlPatterns = "/*", filterName = "HttpServletRequestWrappingFilter")
public class HttpServletRequestWrappingFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		chain.doFilter(
			new StreamReusableHttpServletRequest((HttpServletRequest)request), response);
//			new ContentCachingResponseWrapper((HttpServletResponse) response));
	}
}
