package sk.golddigger.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class PayloadValidationInterceptor implements HandlerInterceptor { // payloadValidationInterceptor --> rename this class to this

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		// implement own httpservletrequest returning servletinputstream in such a way, that it can be retained again after
		// reading.. 
		return true;
	}

	// only for output validation - can extract it from modelAndView
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
		
	}
}
