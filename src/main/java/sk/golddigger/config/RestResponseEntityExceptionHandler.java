package sk.golddigger.config;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import sk.golddigger.exceptions.ClientSideFailure;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger logger = Logger.getLogger(RestResponseEntityExceptionHandler.class);

	@ExceptionHandler({ ClientSideFailure.class })
	public ResponseEntity<Object> handleAccessDeniedException(Exception ex, WebRequest request) {
		return new ResponseEntity<Object>(ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	@PostConstruct
	private void logInit() {
		logger.info(RestResponseEntityExceptionHandler.class + " has been loaded");
	}
}
