package com.github.black0nion.blackonionbot.utils;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PlaceholderTest {

	@Test
	void test_creation_string() throws NoSuchFieldException, IllegalAccessException {
		Placeholder placeholder = new Placeholder("key", "value");

		Field key = Placeholder.class.getDeclaredField("key");
		key.setAccessible(true);
		assertEquals("%key%", key.get(placeholder));

		Field value = Placeholder.class.getDeclaredField("value");
		value.setAccessible(true);
		assertEquals("value", value.get(placeholder));
	}

	@Test
	void test_creation_object() throws NoSuchFieldException, IllegalAccessException {
		Placeholder placeholder = new Placeholder("key", new Object() {
			@Override
			public String toString() {
				return "custom toString";
			}
		});

		Field key = Placeholder.class.getDeclaredField("key");
		key.setAccessible(true);
		assertEquals("%key%", key.get(placeholder));

		Field value = Placeholder.class.getDeclaredField("value");
		value.setAccessible(true);
		assertEquals("custom toString", value.get(placeholder));
	}

	@Test
	void test_process() {
		Placeholder placeholder = new Placeholder("key", "value");
		assertEquals("test value test", placeholder.process("test %key% test"));
		assertEquals("test valuetest", placeholder.process("test %key%test"));
	}

	@Test
	void test_process_no_match() {
		Placeholder placeholder = new Placeholder("key", "value");
		assertEquals("test %otherkey% test", placeholder.process("test %otherkey% test"));
		assertEquals("test %key test", placeholder.process("test %key test"));
		assertEquals("test key% test", placeholder.process("test key% test"));
		assertEquals("test key test", placeholder.process("test key test"));
	}

	@Test
	void test_process_multiple() {
		Placeholder placeholder = new Placeholder("key", "value");
		Placeholder placeholder2 = new Placeholder("key2", "value2");
		assertEquals("test value value2 test", Placeholder.process("test %key% %key2% test", placeholder, placeholder2));
		assertEquals("test value2 value test", Placeholder.process("test %key2% %key% test", placeholder, placeholder2));
	}

	@Test
	void test_process_multiple_no_match() {
		Placeholder placeholder = new Placeholder("key", "value");
		Placeholder placeholder2 = new Placeholder("key2", "value2");
		assertEquals("test %otherkey% value2 test", Placeholder.process("test %otherkey% %key2% test", placeholder, placeholder2));
		assertEquals("test value2 %otherkey% test", Placeholder.process("test %key2% %otherkey% test", placeholder, placeholder2));
		assertEquals("test %otherkey% %otherkey2% test", Placeholder.process("test %otherkey% %otherkey2% test", placeholder, placeholder2));
	}

	@Test
	void test_process_nullInput() {
		Placeholder placeholder = new Placeholder("key", "value");
		assertNull(Placeholder.process(null, placeholder));
	}

	@Test
	void test_process_emptyInput() {
		Placeholder placeholder = new Placeholder("key", "value");
		assertEquals("", Placeholder.process("", placeholder));
	}
}
