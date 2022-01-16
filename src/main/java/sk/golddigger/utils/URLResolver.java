package sk.golddigger.utils;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import sk.golddigger.exceptions.ApplicationFailure;

/**
 * This components's main capability is to replaced the marked content of the
 * URL by desired value.
 * 
 * @see {@link sk.golddigger.enums.Resources} how the substitution marks are applied.
 */
@Component
public class URLResolver {

	private static final Logger logger = Logger.getLogger(URLResolver.class);

	@Value("$")
	private String substitutionMark;

	/**
	 * This method performs the dynamic URL part substitution.</br>
	 * In order to make this functionality work, all parts of the original
	 * URL that should be dynamically substituted <b>must</b> replace the
	 * dynamic part with {@code substitutionMark} property defined by this
	 * class.
	 * 
	 * <pre class="code">
	 * String url = "https://www.$.sk/$/info; // $ is the substitutionMark
	 * String resolvedUrl = resolveParams(url, "golddigger", "page");
	 * // returns https://www.golddigger.sk/page/info
	 * </pre>
	 * 
	 * @param url - string with substitutionMarks
	 * @param params - parameters to fill url substitutionMarks
	 */
	public String resolveParams(String url, Object... params) {
		if (params == null) {
			return url;
		}

		int paramsLength = params.length;
		validateParamsSubstitution(url, paramsLength);

		String resolvedURL;
		if (paramsLength == 1) {
			resolvedURL = url.replace(substitutionMark, params[0].toString());
		} else {
			resolvedURL = substitute(url, params);
		}

		return resolvedURL;
	}

	private void validateParamsSubstitution(String url, int paramsLength) {
		boolean isEmpty = paramsLength == 0;
		boolean isAboveOne = paramsLength > 1;

		if (isEmpty || (isAboveOne && paramsLength != getNumberOfChars(url))) {
			String validationMessage = resolveMessage("urlParamsSubstitutionInconsistency");
			logger.error(validationMessage);
			throw new ApplicationFailure(validationMessage);
		}
	}

	private int getNumberOfChars(String string) {
		return (int) string.chars().filter(e -> e == substitutionMark.charAt(0)).count();
	}

	private String substitute(String url, Object... params) {
		StringBuilder result = new StringBuilder(url);

		for (Object param : params) {
			int indexOfStar = result.indexOf(substitutionMark);
			result.replace(indexOfStar, indexOfStar + 1, param.toString());
		}

		return result.toString();
	}

	/**
	 * This method extracts the http/s protocol
	 * and gives back only the URL path.
	 * @param url - url string
	 * @return full URL path
	 */
	public String resolvePath(String url) {
		if (url == null) {
			return "";
		}

		StringBuilder path = new StringBuilder(url);
		path.delete(0, 8); // remove http protocol prefix
		int pathBeginning = path.indexOf("/");
		return path.substring(pathBeginning);
	}
}
