package com.github.black0nion.blackonionbot.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ChainableAtomicReferenceTest {

	@Test
	void test_set_and_get_with_initial_value() {
		ChainableAtomicReference<String> ref = new ChainableAtomicReference<>("test");
		assertEquals("test", ref.get());
		assertEquals("test2", ref.setAndGet("test2"));
		assertEquals("test2", ref.get());
	}

	@Test
	void test_set_and_get_without_initial_value() {
		ChainableAtomicReference<String> ref = new ChainableAtomicReference<>();
		assertNull(ref.get());
		assertEquals("test2", ref.setAndGet("test2"));
		assertEquals("test2", ref.get());
	}
}
