package sk.golddigger.validation;

import static sk.golddigger.enums.HttpMethod.GET;
import static sk.golddigger.enums.HttpMethod.POST;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import sk.golddigger.exceptions.ClientSideFailure;

@Component
public class PayloadValidator implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		try {
			String httpMethod = request.getMethod();
			if ((GET.equals(httpMethod) || POST.equals(httpMethod)) && request.getInputStream().read() != -1) {
				throw new ClientSideFailure("clientBodyContentNotAllowed"); // add value in message resolver
			} else {
				// get reader from request and validate the payload against schema..
				// but what about the reader...will it not read-out the resource not to be readable again?
			}
		} catch (IOException ioe) {
			// log it
		}
		return true;
	}

	// only for output validation - can extract it from modelAndView
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
		
	}
}
