package com.github.black0nion.blackonionbot.rest;

import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import com.github.black0nion.blackonionbot.oauth.OAuthAPI;
import com.github.black0nion.blackonionbot.oauth.api.SessionHandler;
import com.github.black0nion.blackonionbot.rest.sessions.AbstractSession;
import com.google.gson.Gson;
import io.mokulu.discord.oauth.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.InputMismatchException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbstractSessionTest {
	public static final String EXAMPLE_SESSION_ID = "qUJf7fE7WooHAQUHZzsmk8lLl1HRWHTfn6lQPgDQtdSNrFHvZAf8WoEuqU7KuK72qeyi3";
	public static final String EXAMPLE_CODE = "so3oshW4GldevEQ0cY6a6PRzG0JFIa";
	public static final User EXAMPLE_USER = new Gson().fromJson("""
		{
			"id": "69696969696969696",
			"username": "TestBot",
			"discriminator": "1337",
			"verified": true
		}""", User.class);

	public static class MockedLogin implements SessionHandler {
		private boolean loggedInYet = false;

		@Override
		public DiscordUser loginToSession(String sessionId) throws InputMismatchException, NullPointerException {
			if (sessionId.equals(EXAMPLE_SESSION_ID) && loggedInYet) {
				return new DiscordUser() {{
					this.user = EXAMPLE_USER;
				}};
			}
			throw new InputMismatchException("Invalid session id");
		}

		@Override
		public void logoutFromSession(String sessionId) throws InputMismatchException, NullPointerException {}

		@Override
		public String createSession(String accessToken, String refreshToken, int expiresIn) {
			loggedInYet = true;
			return EXAMPLE_SESSION_ID;
		}

		@Override
		public boolean isIdOccupied(String sessionId) {
			return sessionId.equals(EXAMPLE_SESSION_ID);
		}
	}

	@BeforeEach
	void setup() {
		AbstractSession.setSessionHandler(new MockedLogin());
	}

	@Test
	void test_code_pattern() {
		assertTrue(EXAMPLE_CODE.matches(OAuthAPI.TOKEN_PATTERN.toString()));
		assertFalse("test".matches(OAuthAPI.TOKEN_PATTERN.toString()));
	}
}
