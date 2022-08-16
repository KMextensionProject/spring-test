package sk.golddigger.interceptors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import sk.golddigger.validation.ServletContextSchemaLoader;

@Component
public class PayloadValidationInterceptor implements HandlerInterceptor {

	@Autowired
	private ServletContextSchemaLoader schemaLoader;

	// TODO: add dependency on JSON validation API

	// this should validate input path schema
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		return true;
	}

	// this should validate output path schema
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
		
	}

	@PostConstruct
	private void logInit() {
		System.out.println("JSON payload validating interceptor has been activated");
	}
}
