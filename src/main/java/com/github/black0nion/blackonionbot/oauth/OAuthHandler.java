package com.github.black0nion.blackonionbot.oauth;

import com.github.black0nion.blackonionbot.rest.sessions.AbstractSession;
import com.github.black0nion.blackonionbot.oauth.api.SessionHandler;
import com.github.black0nion.blackonionbot.oauth.api.DiscordAuthCodeToTokens;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.mokulu.discord.oauth.DiscordAPI;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.concurrent.ExecutionException;

import static com.github.black0nion.blackonionbot.oauth.OAuthAPI.*;

public class OAuthHandler {

	private final SessionHandler sessionHandler;
	private final DiscordAuthCodeToTokens discordAuthCodeToTokens;

	public OAuthHandler(SessionHandler sessionHandler, DiscordAuthCodeToTokens discordAuthCodeToTokens) {
		this.sessionHandler = sessionHandler;
		this.discordAuthCodeToTokens = discordAuthCodeToTokens;
	}

	private static final Cache<String, DiscordUser> users = CacheBuilder.newBuilder().build();

	public static DiscordUser getUserWithToken(String access_token) throws Exception {
		return getUserWithToken(access_token, null);
	}

	public static DiscordUser getUserWithToken(String access_token, @NonNullWhen("Loading for the first time") String refresh_token) throws ExecutionException, InputMismatchException {
		if (access_token == null || !TOKEN_PATTERN.matcher(access_token).matches() || (refresh_token != null && !TOKEN_PATTERN.matcher(refresh_token).matches())) throw new InputMismatchException("Either access_token or refresh_token is null or don't match the required pattern!");
		return users.get(access_token, () -> new DiscordUser(access_token, refresh_token, new DiscordAPI(access_token)));
	}

	/**
	 * call once to generate the tokens from the code and save it, only on first login
	 * with discord, on reconnect on the same PC (session) use
	 * {@link AbstractSession#loginToSession(String)}!
	 *
	 * @param code the code discord gave you
	 * @return The session ID used to authenticate with the bot, or null if the login failed
	 */
	@Nullable
	public String loginWithDiscord(final String code) throws IOException, InputMismatchException {
		if (code == null || !TOKEN_PATTERN.matcher(code).matches()) throw new InputMismatchException("Invalid code");

		return discordAuthCodeToTokens.loginWithDiscord(code);
	}
}
