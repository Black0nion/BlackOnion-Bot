package com.github.black0nion.blackonionbot.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IncrementerTest {

	@Test
	void testIncrement() {
		Incrementer incrementer = new Incrementer();
		incrementer.increment();
		assertEquals(1, incrementer.getCount());
	}

	@Test
	void testIncrementObject() {
		Incrementer incrementer = new Incrementer();
		incrementer.increment(new Object());
		assertEquals(1, incrementer.getCount());
	}

	@Test
	void testGetCount() {
		Incrementer incrementer = new Incrementer();
		assertEquals(0, incrementer.getCount());
	}

	@Test
	void testSetCount() {
		Incrementer incrementer = new Incrementer();
		incrementer.setCount(5);
		assertEquals(5, incrementer.getCount());
	}

	@Test
	void testReset() {
		Incrementer incrementer = new Incrementer();
		incrementer.increment();
		incrementer.reset();
		assertEquals(0, incrementer.getCount());
	}

	@Test
	void testToString() {
		Incrementer incrementer = new Incrementer();
		assertEquals("0", incrementer.toString());
	}
}
