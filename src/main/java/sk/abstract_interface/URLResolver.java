package sk.abstract_interface;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * doplnanie parametrov do query ako su tokeny a pod..
 */
@Component
public class URLResolver {

	@Value("*")
	private String substitutionMark;

	/**
	 * 
	 * @param url    - dynamicke casti su zamenene hviezdickou
	 * @param params - ak je len jeden tak sa dosadi vsade
	 */
	public String resolve(String url, Object... params) {
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
			// add message
			throw new IllegalArgumentException("");
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

	public String getRequestPath(String url) {
		StringBuilder sb = new StringBuilder(url);
		sb.delete(0, 8); // remove http protocol prefix
		int pathBeginning = sb.indexOf("/");
		return sb.substring(pathBeginning);
	}
}
