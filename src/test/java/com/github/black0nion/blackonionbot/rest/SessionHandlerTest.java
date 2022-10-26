package com.github.black0nion.blackonionbot.rest;

import com.github.black0nion.blackonionbot.oauth.OAuthHandler;
import com.github.black0nion.blackonionbot.oauth.api.DiscordAuthCodeToTokens;
import com.github.black0nion.blackonionbot.oauth.api.SessionHandler;
import com.github.black0nion.blackonionbot.rest.impl.post.Login;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.black0nion.blackonionbot.rest.AbstractSessionTest.EXAMPLE_CODE;
import static com.github.black0nion.blackonionbot.rest.AbstractSessionTest.EXAMPLE_SESSION_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SessionHandlerTest {
	private static final Logger logger = LoggerFactory.getLogger(SessionHandlerTest.class);

	private final SessionHandler handler = mock(SessionHandler.class);
	private final Context ctx = mock(Context.class);

	@Test
	void test_create_session_id_no_code() {
		DiscordAuthCodeToTokens discordAuthCodeToTokens = mock(DiscordAuthCodeToTokens.class);
		Login login = new Login(new OAuthHandler(handler, discordAuthCodeToTokens));

		assertThrows(BadRequestResponse.class, () -> login.handle(ctx, null, null, null));
	}

	@Test
	void test_create_session_id_invalid_code() throws Exception {
		DiscordAuthCodeToTokens discordAuthCodeToTokens = mock(DiscordAuthCodeToTokens.class);
		when(discordAuthCodeToTokens.loginWithDiscord(EXAMPLE_CODE)).thenReturn(EXAMPLE_SESSION_ID);
		Login login = new Login(new OAuthHandler(handler, discordAuthCodeToTokens));

		when(ctx.header("code")).thenReturn(EXAMPLE_CODE);
		String body = (String) login.handle(ctx, null, null, null);
		assertNotNull(body);

		logger.info("Response Body: " + body);
		assertNotNull(body);
		assertTrue(body.equalsIgnoreCase(EXAMPLE_SESSION_ID));
	}

	@Test
	void test_create_session_id() throws Exception {
		DiscordAuthCodeToTokens discordAuthCodeToTokens = mock(DiscordAuthCodeToTokens.class);
		when(discordAuthCodeToTokens.loginWithDiscord(EXAMPLE_CODE)).thenReturn(EXAMPLE_SESSION_ID);

		Login login = new Login(new OAuthHandler(handler, discordAuthCodeToTokens));

		when(ctx.header("code")).thenReturn(EXAMPLE_CODE);
		String body = (String) login.handle(ctx, null, null, null);
		assertNotNull(body);

		logger.info("Response Body: " + body);
		assertNotNull(body);
		assertTrue(body.equalsIgnoreCase(EXAMPLE_SESSION_ID));
	}
}
