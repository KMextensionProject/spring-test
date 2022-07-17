package sk.golddigger.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class TypeUtilsTest {

	private static Predicate<Integer> greaterThanFour;

	@BeforeAll
	static void initPredicate() {
		greaterThanFour = n -> n > 4;
	}

	@Test
	@DisplayName("Getting the right type based on passing predicate")
	void getValueByConditionReturnTypeTest() {
		Object valueByCondition = TypeUtils.getValueByCondition(greaterThanFour, 5, false);
		assertInstanceOf(Integer.class, valueByCondition, "Should be an Integer");
	}

	@Test
	@DisplayName("Getting the right return value based on not-passing predicate")
	void getValueByConditionReturnValueTest() {
		Object valueByCondition = TypeUtils.getValueByCondition(greaterThanFour, 4, false);
		assertEquals(false, valueByCondition, "Should be false");
	}

	@Test
	@DisplayName("Passing nullable predicate as argument")
	void passingNullablePredicate() {
		Executable executable = () -> TypeUtils.getValueByCondition(null, 4, false);
		assertThrows(IllegalStateException.class, executable, "When passing null, it should be an ApplicationFailureException");
	}

	@Test
	@DisplayName("Passing nulls as argumetns to predicate")
	void passingNullsAsArgumentsToPredicate() {
		Object expected = null;
		Object actual = TypeUtils.getValueByCondition(greaterThanFour, 4, null);
		assertEquals(expected, actual);
	}

}
