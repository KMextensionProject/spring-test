package sk.golddigger.interceptors;

import static sk.golddigger.interceptors.TransactionInjectingFilter.TRANSACTION_ID_REQUEST_ATTRIBUTE;
import static sk.golddigger.utils.MessageResolver.resolveMessage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

	private static final Logger logger = Logger.getLogger(LoggingInterceptor.class);

	private long requestStartTime;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		this.requestStartTime = System.currentTimeMillis();
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
		String transactionId = String.valueOf(request.getAttribute(TRANSACTION_ID_REQUEST_ATTRIBUTE));
		String url = request.getRequestURL().toString();
		long time = System.currentTimeMillis() - requestStartTime;
		logger.info(resolveMessage("requestTimeLog", transactionId, url, time));
	}

}
