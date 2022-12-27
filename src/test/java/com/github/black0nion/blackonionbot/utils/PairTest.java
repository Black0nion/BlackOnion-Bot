package com.github.black0nion.blackonionbot.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PairTest {
	@Test
	void test() {
		Pair<String, Integer> pair = new Pair<>("test", 5);
		assertEquals("test", pair.getFirst());
		assertEquals(5, pair.getSecond());
	}

	@Test
	void test_to_string() {
		Pair<String, Integer> pair = new Pair<>("test", 5);
		assertEquals("Pair{first=test, second=5}", pair.toString());
	}

	@Test
	void test_null_values() {
		Pair<String, Integer> pair = new Pair<>(null, null);
		assertNull(pair.getFirst());
		assertNull(pair.getSecond());
	}

	@Test
	void test_set() {
		Pair<String, Integer> pair = new Pair<>("test", 5);
		pair.setFirst("test2");
		pair.setSecond(6);
		assertEquals("test2", pair.getFirst());
		assertEquals(6, pair.getSecond());
	}
}
