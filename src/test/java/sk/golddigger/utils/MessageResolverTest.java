package sk.golddigger.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sk.golddigger.utils.MessageResolver.resolveMessage;

import org.junit.jupiter.api.Test;

class MessageResolverTest {

	@Test
	void noIndexNoArgument() {
		String expected = "test";
		String actual = resolveMessage("unitTest_1");
		assertEquals(expected, actual);
	}

	@Test
	void oneIndexOneArgument() {
		String expected = "test - OK";
		String actual = resolveMessage("unitTest_2", "OK");
		assertEquals(expected, actual);
	}

	@Test
	void multipleIndicesOneArgument() {
		// not resolving values when not provided -> don't want to resolve all indices
		// by one value
		String expected = "tests [OK, {1}, {2}]";
		String actual = resolveMessage("unitTest_3", "OK");
		assertEquals(expected, actual);
	}

	@Test
	void oneIndexMultipleArguments() {
		String expected = "test OK"; // resolving just first
		String actual = resolveMessage("unitTest_4", "OK", "NOT_OK", "MAYBE_OK");
		assertEquals(expected, actual);
	}

	@Test
	void multipleIndicesMultipleArgument() {
		String expected = "tests passed: 3";
		String actual = resolveMessage("unitTest_5", "test", 3, "passed");
		assertEquals(expected, actual);
	}
}
