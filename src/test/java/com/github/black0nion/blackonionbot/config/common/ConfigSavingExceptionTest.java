package com.github.black0nion.blackonionbot.config.common;

import com.github.black0nion.blackonionbot.config.common.exception.ConfigException;
import com.github.black0nion.blackonionbot.config.common.exception.ConfigSavingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigSavingExceptionTest {

	@Test
	void test_constructor() {
		ConfigSavingException exception = assertDoesNotThrow(() -> new ConfigSavingException());
		assertInstanceOf(ConfigException.class, exception);
	}

	@Test
	void test_constructor_string() {
		ConfigSavingException exception = assertDoesNotThrow(() -> new ConfigSavingException("Test"));
		assertInstanceOf(ConfigException.class, exception);
		assertEquals("Test", exception.getMessage());
	}

	@Test
	void test_constructor_throwable() {
		ConfigSavingException exception = assertDoesNotThrow(() -> new ConfigSavingException(new NumberFormatException()));
		assertInstanceOf(ConfigException.class, exception);
		assertInstanceOf(NumberFormatException.class, exception.getCause());
	}

	@Test
	void test_constructor_string_throwable() {
		ConfigSavingException exception = assertDoesNotThrow(() -> new ConfigSavingException("Test", new NumberFormatException()));
		assertInstanceOf(ConfigException.class, exception);
		assertEquals("Test", exception.getMessage());
		assertInstanceOf(NumberFormatException.class, exception.getCause());
	}
}
