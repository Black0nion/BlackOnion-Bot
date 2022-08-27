package com.github.black0nion.blackonionbot.rest;

import com.github.black0nion.blackonionbot.oauth.OAuthHandler;
import com.github.black0nion.blackonionbot.oauth.api.DiscordAuthCodeToTokens;
import com.github.black0nion.blackonionbot.oauth.api.SessionHandler;
import com.github.black0nion.blackonionbot.rest.impl.post.Login;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.util.ContextUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.github.black0nion.blackonionbot.rest.AbstractSessionTest.EXAMPLE_CODE;
import static com.github.black0nion.blackonionbot.rest.AbstractSessionTest.EXAMPLE_SESSION_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SessionHandlerTest {
	private static final Logger log = LoggerFactory.getLogger(SessionHandlerTest.class);

	private final SessionHandler handler = mock(SessionHandler.class);
	private final Context ctx = ContextUtil.init(mock(HttpServletRequest.class), mock(HttpServletResponse.class));

	@Test
	void test_create_session_id_no_code() {
		DiscordAuthCodeToTokens discordAuthCodeToTokens = mock(DiscordAuthCodeToTokens.class);
		Login login = new Login(new OAuthHandler(handler, discordAuthCodeToTokens));

		assertThrows(BadRequestResponse.class, () -> login.handle(ctx, null, null, null));
	}

	@Test
	void test_create_session_id_invalid_code() throws Exception {
		DiscordAuthCodeToTokens discordAuthCodeToTokens = mock(DiscordAuthCodeToTokens.class);
		when(discordAuthCodeToTokens.loginWithDiscord(eq(EXAMPLE_CODE))).thenReturn(EXAMPLE_SESSION_ID);
		Login login = new Login(new OAuthHandler(handler, discordAuthCodeToTokens));

		when(ctx.header("code")).thenReturn(EXAMPLE_CODE);
		String body = (String) login.handle(ctx, null, null, null);
		assertNotNull(body);

		log.info("Response Body: " + body);
		assertNotNull(body);
		assertTrue(body.equalsIgnoreCase(EXAMPLE_SESSION_ID));
	}

	@Test
	void test_create_session_id() throws Exception {
		DiscordAuthCodeToTokens discordAuthCodeToTokens = mock(DiscordAuthCodeToTokens.class);
		when(discordAuthCodeToTokens.loginWithDiscord(eq(EXAMPLE_CODE))).thenReturn(EXAMPLE_SESSION_ID);

		Login login = new Login(new OAuthHandler(handler, discordAuthCodeToTokens));

		when(ctx.header("code")).thenReturn(EXAMPLE_CODE);
		String body = (String) login.handle(ctx, null, null, null);
		assertNotNull(body);

		log.info("Response Body: " + body);
		assertNotNull(body);
		assertTrue(body.equalsIgnoreCase(EXAMPLE_SESSION_ID));
	}
}
