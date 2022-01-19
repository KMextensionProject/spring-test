package sk.golddigger.interceptors;

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

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		this.requestStartTime = System.currentTimeMillis();
		return true;
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
		String url = request.getRequestURL().toString();
		long time = System.currentTimeMillis() - requestStartTime;
		logger.info(resolveMessage("requestTimeLog", url, time));
	}

}
