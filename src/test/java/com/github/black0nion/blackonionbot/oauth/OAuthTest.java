package com.github.black0nion.blackonionbot.oauth;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.InputMismatchException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OAuthTest {
	@Test
	@BeforeAll
	public static void test_oauth_api_exists() {
		assertNotNull(OAuthUtils.OAUTH_HANDLER);
	}

	@Test
	public void test_invalid_tokens() {
		assertThrows(InputMismatchException.class, () -> OAuthUtils.getUserWithToken("dummy", ""));
		assertThrows(InputMismatchException.class, () -> OAuthUtils.getUserWithToken(null, ""));
		assertThrows(InputMismatchException.class, () -> OAuthUtils.getUserWithToken("", null));
		assertThrows(InputMismatchException.class, () -> OAuthUtils.getUserWithToken("hi"));
		assertThrows(InputMismatchException.class, () -> OAuthUtils.getUserWithToken(null));
	}
}
