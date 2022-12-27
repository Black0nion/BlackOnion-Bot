package com.github.black0nion.blackonionbot.tests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SameObjectTest {

	@Test
	void test_equals() {
		SameObject obj1 = new SameObject();
		SameObject obj2 = new SameObject();

		assertNotSame(obj1, obj2);
		assertNotEquals(obj1, obj2);
	}

	private static final class TestObj {
		@Override
		public boolean equals(Object obj) {
			return obj == this;
		}
	}

	@Test
	void test_equals_obj() {
		Object obj = new TestObj();

		SameObject obj1 = new SameObject(obj);
		SameObject obj2 = new SameObject(obj);

		assertEquals(obj1, obj2);

		assertNotEquals(obj1, new SameObject());
		assertNotEquals(obj2, new SameObject());

		assertNotEquals(obj1, new SameObject(new TestObj()));
		assertNotEquals(obj2, new SameObject(new TestObj()));
	}
}
