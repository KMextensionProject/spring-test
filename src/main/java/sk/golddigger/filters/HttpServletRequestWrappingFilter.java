package sk.golddigger.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import sk.golddigger.http.StreamReusableHttpServletRequest;

@WebFilter(urlPatterns = "/*", filterName = "HttpServletRequestWrappingFilter")
public class HttpServletRequestWrappingFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO: do the same with the HttpServletResponse object
		chain.doFilter(new StreamReusableHttpServletRequest((HttpServletRequest)request), response);		
	}
}
