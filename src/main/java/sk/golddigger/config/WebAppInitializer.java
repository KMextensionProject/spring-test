package sk.golddigger.config;

import static sk.golddigger.enums.Resources.JSP_VIEW_RESOURCES;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import sk.golddigger.interceptors.LoggingInterceptor;

@Component
public class WebAppInitializer implements WebMvcConfigurer {

	private static final Logger logger = Logger.getLogger(WebAppInitializer.class);

	@Autowired
	private LoggingInterceptor loggingInterceptor;

	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		registry.jsp(JSP_VIEW_RESOURCES, ".jsp");
		logger.info("JSP view resolver has been registered to look for resources in " + JSP_VIEW_RESOURCES);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loggingInterceptor);
		logger.info("Interceptor for logging HTTP requests has been registered");
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(0, getJackson2HttpMessageConverter());
		logger.info("The default HttpMessageConverter has been overriden by Jackson2HttpMessageConverter with output indentation");
	}

	@Bean
	public MappingJackson2HttpMessageConverter getJackson2HttpMessageConverter() {
		MappingJackson2HttpMessageConverter jackson2HttpMsgConverter = new MappingJackson2HttpMessageConverter();
		jackson2HttpMsgConverter.setObjectMapper(getObjectMapper());
		return jackson2HttpMsgConverter;
	}

	@Bean
	public ObjectMapper getObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		return objectMapper;
	}
}
