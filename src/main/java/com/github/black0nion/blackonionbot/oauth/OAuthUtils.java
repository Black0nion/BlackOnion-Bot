package com.github.black0nion.blackonionbot.oauth;

import com.github.black0nion.blackonionbot.api.sessions.AbstractSession;
import com.github.black0nion.blackonionbot.api.sessions.LoginFactory;
import com.github.black0nion.blackonionbot.utils.config.Config;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.mokulu.discord.oauth.DiscordAPI;
import io.mokulu.discord.oauth.DiscordOAuth;
import io.mokulu.discord.oauth.model.TokensResponse;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class OAuthUtils {

	private static final String BASE_URL = "https://discord.com/api/oauth2";
	private static final String TOKEN_URL = BASE_URL + "/token";
	private static final String TOKEN_REVOKE_URL = BASE_URL + "/revoke";
	private static final String[] SCOPES = { "identify", "guilds" };
	private static final String GRANT_TYPE = "authorization_code";
	private static final String REDIRECT_URI = Config.getInstance().getDiscordappRedirectUrl();
	private static final String CLIENT_ID = Config.getInstance().getDiscordappClientId();
	private static final String CLIENT_SECRET = Config.getInstance().getDiscordappClientSecret();

	public static final DiscordOAuth OAUTH_HANDLER = new DiscordOAuth(CLIENT_ID, CLIENT_SECRET, REDIRECT_URI, SCOPES);

	public static final Pattern TOKEN_PATTERN = Pattern.compile("[a-zA-Z\\d]{30,32}");

	public static String getAuthorizeURL() {
		return OAUTH_HANDLER.getAuthorizationURL(null);
	}

	public static String getTokenURL(String code) {
		return TOKEN_URL + "?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&grant_type=" + GRANT_TYPE + "&code=" + code + "&redirect_uri=" + REDIRECT_URI;
	}

	public static String getTokenRevokeURL(String token) {
		return TOKEN_REVOKE_URL + "?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&token=" + token;
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
	public static String loginWithDiscord(final String code) throws IOException, InputMismatchException {
		if (code == null || !TOKEN_PATTERN.matcher(code).matches()) throw new InputMismatchException("Invalid code");

		return impl.loginWithDiscord(code);
	}

	private static ILoginWithCode impl = code -> {
		final TokensResponse response = OAUTH_HANDLER.getTokens(code);
		if (response == null) return null;
		else return LoginFactory.getImpl().createSession(response.getAccessToken(), response.getRefreshToken(), response.getExpiresIn());
	};
	public static void setLoginWithCodeImplementation(ILoginWithCode impl) {
		OAuthUtils.impl = impl;
	}

	public interface ILoginWithCode {
		String loginWithDiscord(String code) throws IOException, InputMismatchException;
	}
}