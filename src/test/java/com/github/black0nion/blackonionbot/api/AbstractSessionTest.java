package com.github.black0nion.blackonionbot.api;

import com.github.black0nion.blackonionbot.api.sessions.AbstractSession;
import com.github.black0nion.blackonionbot.api.sessions.LoginFactory;
import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import com.github.black0nion.blackonionbot.oauth.OAuthUtils;
import com.google.gson.Gson;
import io.mokulu.discord.oauth.model.User;
import org.junit.jupiter.api.*;

import java.util.InputMismatchException;

import static org.junit.jupiter.api.Assertions.*;

@Order(21)
public class AbstractSessionTest {
	public static final String EXAMPLE_SESSION_ID = "qUJf7fE7WooHAQUHZzsmk8lLl1HRWHTfn6lQPgDQtdSNrFHvZAf8WoEuqU7KuK72qeyi3";
	public static final String VALID_UNKNOWN_SESSION_ID = "qUJf7fE7WooHAQUHZzsmk8lLl1HRWHTfn6lQPgDQtdSNrFHvZAf8WoEuqU7KuK72qeyi4";
	public static final String EXAMPLE_CODE = "so3oshW4GldevEQ0cY6a6PRzG0JFIa";
	public static final User EXAMPLE_USER = new Gson().fromJson("""
		{
			"id": "69696969696969696",
			"username": "TestBot",
			"discriminator": "1337",
			"verified": true
		}""", User.class);

	@BeforeAll
	public static void init() {
		LoginFactory.setInstance(() -> new LoginFactory.ILogin() {
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
		});
	}

	@RepeatedTest(50)
	@DisplayName("test id generation")
	void test_id_generation(RepetitionInfo info) {
		for (int i = 0; i < info.getTotalRepetitions(); i++) {
			assertTrue(assertDoesNotThrow(AbstractSession::generateSessionId).matches(AbstractSession.SESSIONID_REGEX));
		}
	}

	@Test
	void test_code_pattern() {
		assertTrue(EXAMPLE_CODE.matches(OAuthUtils.TOKEN_PATTERN.toString()));
		assertFalse("test".matches(OAuthUtils.TOKEN_PATTERN.toString()));
	}
}