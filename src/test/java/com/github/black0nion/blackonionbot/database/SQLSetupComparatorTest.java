package com.github.black0nion.blackonionbot.database;

import com.github.black0nion.blackonionbot.misc.SQLSetup;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SQLSetupComparatorTest {

	private static final class EmptyArray {
		@SQLSetup
		private static void setup() {}
	}

	private static final class EmptyArrayAfter {
		@SQLSetup(after = EmptyArray.class)
		private static void setup() {}
	}

	private static final class EmptyArrayAfterAfter {
		@SQLSetup(after = EmptyArrayAfter.class)
		private static void setup() {}
	}

	private static final class RecursiveArrayAfterOne {
		@SQLSetup(after = RecursiveArrayAfterTwo.class)
		private static void setup() {}
	}

	private static final class RecursiveArrayAfterTwo {
		@SQLSetup(after = RecursiveArrayAfterOne.class)
		private static void setup() {}
	}

	private static final class DependsOnSelf {
		@SQLSetup(after = DependsOnSelf.class)
		private static void setup() {}
	}

	private static <T> Method getMethod(Class<T> clazz) throws NoSuchMethodException {
		return clazz.getDeclaredMethod("setup");
	}

	@Test
	void test_compare_normal() throws NoSuchMethodException {
		SQLSetupComparator comparator = SQLSetupComparator.INSTANCE;

		assertEquals(0, comparator.compare(getMethod(EmptyArray.class), getMethod(EmptyArray.class)));
		assertEquals(1, comparator.compare(getMethod(EmptyArrayAfter.class), getMethod(EmptyArray.class)));
		assertEquals(-1, comparator.compare(getMethod(EmptyArray.class), getMethod(EmptyArrayAfter.class)));
		assertEquals(1, comparator.compare(getMethod(EmptyArrayAfterAfter.class), getMethod(EmptyArray.class)));
		assertEquals(-1, comparator.compare(getMethod(EmptyArray.class), getMethod(EmptyArrayAfterAfter.class)));
		assertEquals(1, comparator.compare(getMethod(EmptyArrayAfterAfter.class), getMethod(EmptyArrayAfter.class)));
		assertEquals(-1, comparator.compare(getMethod(EmptyArrayAfter.class), getMethod(EmptyArrayAfterAfter.class)));
	}

	@Test
	void test_compare_recursive() throws NoSuchMethodException {
		SQLSetupComparator comparator = SQLSetupComparator.INSTANCE;

		assertEquals(1, comparator.compare(getMethod(RecursiveArrayAfterOne.class), getMethod(RecursiveArrayAfterTwo.class)));
		assertEquals(1, comparator.compare(getMethod(RecursiveArrayAfterTwo.class), getMethod(RecursiveArrayAfterOne.class)));
	}

	@Test
	void test_compare_withList() throws NoSuchMethodException {
		SQLSetupComparator comparator = SQLSetupComparator.INSTANCE;

		List<Method> list = Arrays.asList(
			getMethod(RecursiveArrayAfterOne.class),
			getMethod(EmptyArrayAfter.class),
			getMethod(RecursiveArrayAfterTwo.class),
			getMethod(EmptyArray.class),
			getMethod(EmptyArrayAfterAfter.class)
		);

		list.sort(comparator);

		LoggerFactory.getLogger(SQLSetupComparatorTest.class).debug("List is: {}", list);

		int emptyArrayIndex = list.indexOf(getMethod(EmptyArray.class));
		int emptyArrayAfterIndex = list.indexOf(getMethod(EmptyArrayAfter.class));
		int emptyArrayAfterAfterIndex = list.indexOf(getMethod(EmptyArrayAfterAfter.class));

		assertTrue(emptyArrayIndex < emptyArrayAfterIndex);
		assertTrue(emptyArrayAfterIndex < emptyArrayAfterAfterIndex);
	}
}
