package com.github.black0nion.blackonionbot.config.generic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigLoadingExceptionTest {

	@Test
	void test_constructor() {
		ConfigLoadingException exception = assertDoesNotThrow(() -> new ConfigLoadingException());
		assertInstanceOf(ConfigException.class, exception);
	}

	@Test
	void test_constructor_string() {
		ConfigLoadingException exception = assertDoesNotThrow(() -> new ConfigLoadingException("Test"));
		assertInstanceOf(ConfigException.class, exception);
		assertEquals("Test", exception.getMessage());
	}

	@Test
	void test_constructor_throwable() {
		ConfigLoadingException exception = assertDoesNotThrow(() -> new ConfigLoadingException(new NumberFormatException()));
		assertInstanceOf(ConfigException.class, exception);
		assertInstanceOf(NumberFormatException.class, exception.getCause());
	}

	@Test
	void test_constructor_string_throwable() {
		ConfigLoadingException exception = assertDoesNotThrow(() -> new ConfigLoadingException("Test", new NumberFormatException()));
		assertInstanceOf(ConfigException.class, exception);
		assertEquals("Test", exception.getMessage());
		assertInstanceOf(NumberFormatException.class, exception.getCause());
	}
}
