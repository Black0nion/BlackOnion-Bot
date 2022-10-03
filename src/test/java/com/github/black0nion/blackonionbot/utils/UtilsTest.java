package com.github.black0nion.blackonionbot.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

	@Test
	void testReplaceException_nullArgumentsThrow() {
		assertThrows(NullPointerException.class, () -> Utils.replaceException(null, Exception.class, RuntimeException.class));
		assertThrows(NullPointerException.class, () -> Utils.replaceException(() -> null, null, RuntimeException.class));
		assertThrows(NullPointerException.class, () -> Utils.replaceException(() -> null, Exception.class, null));
		assertThrows(NullPointerException.class, () -> Utils.replaceException(() -> null, null, RuntimeException.class));
		assertThrows(NullPointerException.class, () -> Utils.replaceException(null, null, null));
	}

	@Test
	void testReplaceException_sameException() {
		NullPointerException exception = new NullPointerException();
		Throwable thrown = assertThrows(Exception.class, () -> Utils.replaceException(() -> { throw exception; }, NullPointerException.class, NullPointerException.class));
		assertSame(exception, thrown);
	}
}
