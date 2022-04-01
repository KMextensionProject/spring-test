package sk.golddigger.config;

import org.apache.log4j.Logger;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * This class provides pre-controller check on class patterns due to recently discovered
 * Spring Framework shell exploit CVE-2022-22965.
 */
@ControllerAdvice
public class BinderControllerAdvice {

	private static final Logger logger = Logger.getLogger(BinderControllerAdvice.class);

	static {
		// TODO: remove when spring patch gets released and upgrade its version
		logger.info("CVE-2022-22965 patch has been loaded.");
	}

	/**
	 * This binder gets called only when there is request payload binded to model object.
	 */
	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		String[] denylist = new String[] { "class.*", "Class.*", "*.class.*", "*.Class.*" };
		dataBinder.setDisallowedFields(denylist);
	}
}