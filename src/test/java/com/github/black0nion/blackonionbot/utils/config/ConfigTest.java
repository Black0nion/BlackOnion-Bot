package com.github.black0nion.blackonionbot.utils.config;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.github.black0nion.blackonionbot.utils.config.Flags.*;
import static org.junit.jupiter.api.Assertions.*;

@Order(1)
class ConfigTest {

	private static final String DUMMY_TOKEN = "MwyPQaFn6VoBaOWQjGfA0ZEX.rGKAZN.mQwvNPMnrLzdv2PTZDwxFy-QkBO";

	@Order(1)
	@Test
	void test_set_properties() {
		// don't worry, this is a generated dummy token
		ConfigManager.set("token", DUMMY_TOKEN);
		assertEquals(DUMMY_TOKEN, System.getProperty("TOKEN"));
		ConfigManager.set("mongo_connection_string", "mongodb://localhost:27017");
		assertEquals("mongodb://localhost:27017", System.getProperty("MONGO_CONNECTION_STRING"));
		ConfigManager.set("test_number", "100");
	}

	@Order(2)
	@Test
	void test_config_construction() {
		new Config();
		assertNotNull(Config.getInstance());
	}


	@Test
	void test_config_get_valid() {
		assertEquals(DUMMY_TOKEN, Config.get("token", String.class));
		assertEquals("default_value", Config.get("not_existing_key", String.class, defaultValue("default_value")));

		assertDoesNotThrow(() -> Config.get("token", String.class, matchesRegex("[MN][A-Za-z\\d]{23}\\.[\\w-]{6}\\.[\\w-]{27}")));
		assertEquals(100, assertDoesNotThrow(() -> Config.get("test_number", Integer.class)));
		assertDoesNotThrow(() -> Config.get("test_number", Integer.class, range(0, 100)));
	}

	@Test
	void test_config_get_invalid() {
		assertThrows(IllegalArgumentException.class, () -> Config.get("token", String.class, matchesRegex("^\\d$")));
		assertThrows(IllegalArgumentException.class, () -> Config.get("test_number", Integer.class, range(0, 10)));
		assertThrows(NumberFormatException.class, () -> Config.get("token", Integer.class));
		assertThrows(IllegalArgumentException.class, () -> Config.get("not_existing", String.class, NonNull));
	}

	@Test
	void test_config_loading() throws IOException {
		ConfigManager.loadConfig();
	}

	@Test
	void test_token_and_mongodb_existing() {
		assertNotNull(Config.getInstance().getToken());
		assertNotNull(Config.getInstance().getMongoConnectionString());
	}
}