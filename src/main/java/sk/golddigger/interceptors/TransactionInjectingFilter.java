package sk.golddigger.interceptors;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;

import sk.golddigger.utils.UUIDHelper;

@WebFilter(urlPatterns = "/*", filterName = "TransactionIdInjectingFilter")
@Order(1)
public class TransactionInjectingFilter implements Filter {

	public static final String TRANSACTION_ID_REQUEST_ATTRIBUTE = "TransactionId";

	// TODO: add TransactionId into a logger definition and then put it into MDC object so the logger can take it from there?

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		String tid = UUIDHelper.generateRandomUUIDnoDashes();
		request.setAttribute(TRANSACTION_ID_REQUEST_ATTRIBUTE, tid);

		if (response instanceof HttpServletResponse) {
			((HttpServletResponse) response).addHeader("X-TransactionId", tid);
		}

		chain.doFilter(request, response);
	}
}
