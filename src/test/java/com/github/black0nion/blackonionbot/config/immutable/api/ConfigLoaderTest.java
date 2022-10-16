package com.github.black0nion.blackonionbot.config.immutable.api;

import com.github.black0nion.blackonionbot.config.immutable.ConfigFlag;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigLoaderTest {

	@Test
	void test_default_method() {
		// Feel free to change this to Mockito, for some reason I couldn't get it to work...

		AtomicBoolean didCall = new AtomicBoolean();
		ConfigLoader configLoader = new ConfigLoader() {
			@Override
			public <T> T get(String name, Class<T> clazz, ConfigFlag... flagsArr) {
				assertSame(ConfigLoader.EMPTY_FLAGS_ARR, flagsArr);
				didCall.set(true);
				return null;
			}
		};
		configLoader.get("test", Object.class);
		assertTrue(didCall.get());
	}
}
