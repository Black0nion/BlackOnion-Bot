package com.github.black0nion.blackonionbot.oauth;

import com.github.black0nion.blackonionbot.config.immutable.api.Config;
import org.junit.jupiter.api.Test;

import java.util.InputMismatchException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class OAuthTest {

	@Test
	void test_oauth_api_exists() {
		assertNotNull(new OAuthAPI(mock(Config.class)).getOAuthApi());
	}

	@Test
	void test_invalid_tokens() {
		assertThrows(InputMismatchException.class, () -> OAuthHandler.getUserWithToken("dummy", ""));
		assertThrows(InputMismatchException.class, () -> OAuthHandler.getUserWithToken(null, ""));
		assertThrows(InputMismatchException.class, () -> OAuthHandler.getUserWithToken("", null));
		assertThrows(InputMismatchException.class, () -> OAuthHandler.getUserWithToken("hi"));
		assertThrows(InputMismatchException.class, () -> OAuthHandler.getUserWithToken(null));
	}
}
