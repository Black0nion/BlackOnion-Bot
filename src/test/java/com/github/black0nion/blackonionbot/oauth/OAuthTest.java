package com.github.black0nion.blackonionbot.oauth;

import com.github.black0nion.blackonionbot.api.sessions.LoginFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.InputMismatchException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Order(2)
class OAuthTest {
	@Test
	@BeforeAll
	static void test_oauth_api_exists() {
		OAuthUtils.setLoginWithCodeImplementation((code) -> LoginFactory.getImpl().createSession(code, new StringBuilder(code).reverse().toString(), -1));
		assertNotNull(OAuthUtils.OAUTH_HANDLER);
	}

	@Test
	void test_invalid_tokens() {
		assertThrows(InputMismatchException.class, () -> OAuthUtils.getUserWithToken("dummy", ""));
		assertThrows(InputMismatchException.class, () -> OAuthUtils.getUserWithToken(null, ""));
		assertThrows(InputMismatchException.class, () -> OAuthUtils.getUserWithToken("", null));
		assertThrows(InputMismatchException.class, () -> OAuthUtils.getUserWithToken("hi"));
		assertThrows(InputMismatchException.class, () -> OAuthUtils.getUserWithToken(null));
	}
}