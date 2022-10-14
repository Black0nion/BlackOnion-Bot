package com.github.black0nion.blackonionbot.utils.config;

import com.github.black0nion.blackonionbot.config.immutable.ConfigFileLoader;
import com.github.black0nion.blackonionbot.config.immutable.impl.ConfigLoaderImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.github.black0nion.blackonionbot.config.immutable.Flags.*;
import static org.junit.jupiter.api.Assertions.*;

// TODO: add unit tests for flags
class ConfigImplTest {

	private static final String DUMMY_TOKEN = "MwyPQaFn6VoBaOWQjGfA0ZEX.rGKAZN.mQwvNPMnrLzdv2PTZDwxFy-QkBO";

	@BeforeAll
	static void setup() {
		// prevents the tests from failing if the .env file exists and contains values
		System.setProperty("SKIP_LOADING_ENV_FILE", "true");
	}

	@Test
	void test_set_properties() {
		// don't worry, this is a generated dummy token
		ConfigFileLoader.set("token", DUMMY_TOKEN);
		assertEquals(DUMMY_TOKEN, System.getProperty("TOKEN"));
		ConfigFileLoader.set("mongo_connection_string", "mongodb://localhost:27017");
		assertEquals("mongodb://localhost:27017", System.getProperty("MONGO_CONNECTION_STRING"));
		ConfigFileLoader.set("test_number", "100");
	}

	@Test
	void test_config_get_valid() {
		assertEquals(DUMMY_TOKEN, ConfigLoaderImpl.getImpl("token", String.class));
		assertEquals("default_value", ConfigLoaderImpl.getImpl("not_existing_key", String.class, defaultValue("default_value")));

		assertDoesNotThrow(() -> ConfigLoaderImpl.getImpl("token", String.class, matchesRegex("[MN][A-Za-z\\d]{23}\\.[\\w-]{6}\\.[\\w-]{27}")));
		assertEquals(100, assertDoesNotThrow(() -> ConfigLoaderImpl.getImpl("test_number", Integer.class)));
		assertDoesNotThrow(() -> ConfigLoaderImpl.getImpl("test_number", Integer.class, range(0, 100)));
	}

	@Test
	void test_config_get_invalid() {
		MatchesRegex matcher = assertDoesNotThrow(() -> matchesRegex("^\\d$"));
		assertThrows(IllegalArgumentException.class, () -> ConfigLoaderImpl.getImpl("token", String.class, matcher));

		Range range = assertDoesNotThrow(() -> range(0, 10));
		assertThrows(IllegalArgumentException.class, () -> ConfigLoaderImpl.getImpl("test_number", Integer.class, range));
		assertThrows(NumberFormatException.class, () -> ConfigLoaderImpl.getImpl("token", Integer.class));
		assertThrows(IllegalArgumentException.class, () -> ConfigLoaderImpl.getImpl("not_existing", String.class, NonNull));
	}

	@Test
	void test_config_loading() {
		assertDoesNotThrow(ConfigFileLoader::loadConfig);
	}
}
