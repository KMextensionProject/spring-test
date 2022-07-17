package sk.golddigger.utils;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import java.util.function.Predicate;

public class TypeUtils {

	private TypeUtils() {
		throw new IllegalStateException(resolveMessage("factoryClassInstantiationError", TypeUtils.class));
	}

	/**
	 * This method evaluates value against predicate and if it succeeds
	 * then it is returned, otherwise the secondary value provided gets
	 * returned instead.
	 */
	public static <T> Object getValueByCondition(Predicate<T> condition, T trueValue, Object falseValue) {
		if (condition == null) {
			throw new IllegalStateException("Condition can not be null");
		}
		return condition.test(trueValue) ? trueValue : falseValue;
	}

}
