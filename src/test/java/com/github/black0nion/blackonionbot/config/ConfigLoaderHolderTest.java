package com.github.black0nion.blackonionbot.config;

import com.github.black0nion.blackonionbot.config.common.ConfigLoader;
import com.github.black0nion.blackonionbot.config.mutable.api.MutableConfigLoader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

class ConfigLoaderHolderTest {

	@Test
	void test_constructor_ConfigLoader() {
		ConfigLoader mock = mock(ConfigLoader.class);
		ConfigLoaderHolder<ConfigLoader> loader = new ConfigLoaderHolder<>(mock);
		assertSame(mock, loader.configLoader);
	}

	@Test
	void test_constructor_MutableConfigLoader() {
		MutableConfigLoader mock = mock(MutableConfigLoader.class);
		ConfigLoaderHolder<ConfigLoader> loader = new ConfigLoaderHolder<>(mock);
		assertSame(mock, loader.configLoader);
	}
}
