package sk.golddigger.utils;

import java.util.function.Predicate;

public class TypeUtils {

	/**
	 * This method evaluates value against predicate and if it succeeds
	 * then it is returned, otherwise the secondary value provided gets
	 * returned instead.
	 */
	public static <T> Object getValueByCondition(Predicate<T> condition, T trueValue, Object falseValue) {
		return condition.test(trueValue) ? trueValue : falseValue;
	}

}
