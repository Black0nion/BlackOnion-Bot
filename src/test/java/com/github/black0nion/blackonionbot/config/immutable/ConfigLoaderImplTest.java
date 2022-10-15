package com.github.black0nion.blackonionbot.config.immutable;

import com.github.black0nion.blackonionbot.config.immutable.impl.ConfigLoaderImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.github.black0nion.blackonionbot.config.immutable.Flags.*;
import static org.junit.jupiter.api.Assertions.*;

// TODO: add unit tests for flags
class ConfigLoaderImplTest {

	// don't worry, this is a generated dummy token
	private static final String DUMMY_TOKEN = "MwyPQaFn6VoBaOWQjGfA0ZEX.rGKAZN.mQwvNPMnrLzdv2PTZDwxFy-QkBO";

	@Test
	void test_set_properties() {
		ConfigFileLoader.set("token", DUMMY_TOKEN);
		assertEquals(DUMMY_TOKEN, System.getProperty("TOKEN"));
	}

	@Test
	void test_config_get_valid() {
		assertEquals(DUMMY_TOKEN, ConfigLoaderImpl.parse("token", DUMMY_TOKEN, String.class));
		assertEquals("default_value", ConfigLoaderImpl.parse("not_existing_key", null, String.class, defaultValue("default_value")));

		assertDoesNotThrow(() -> ConfigLoaderImpl.parse("token", DUMMY_TOKEN, String.class, matchesRegex("[MN][A-Za-z\\d]{23}\\.[\\w-]{6}\\.[\\w-]{27}")));
		assertEquals(100, assertDoesNotThrow(() -> ConfigLoaderImpl.parse("test_number", "100", Integer.class)));
	}

	@Test
	void test_parse() {
		MatchesRegex matcher = assertDoesNotThrow(() -> matchesRegex("^\\d$"));
		assertThrows(IllegalArgumentException.class, () -> ConfigLoaderImpl.parse("token", "iamadummy", String.class, matcher));

		Range range = assertDoesNotThrow(() -> range(0, 10));
		assertThrows(IllegalArgumentException.class, () -> ConfigLoaderImpl.parse("test_number", "20", Integer.class, range));
		assertDoesNotThrow(() -> ConfigLoaderImpl.parse("test_number", "5", Integer.class, range));
		assertDoesNotThrow(() -> ConfigLoaderImpl.parse("test_number", "0", Integer.class, range));
		assertDoesNotThrow(() -> ConfigLoaderImpl.parse("test_number", "10", Integer.class, range));

		assertThrows(NumberFormatException.class, () -> ConfigLoaderImpl.parse("token", "notanumber", Integer.class));
		assertThrows(IllegalArgumentException.class, () -> ConfigLoaderImpl.parse("not_existing", null, String.class, NonNull));
	}

	@Test
	void test_config_loading() {
		assertDoesNotThrow(ConfigFileLoader::loadConfig);
	}
}
