package com.github.black0nion.blackonionbot.oauth;

import com.github.black0nion.blackonionbot.utils.config.Config;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.mokulu.discord.oauth.DiscordAPI;
import io.mokulu.discord.oauth.DiscordOAuth;

public class OAuthUtils {

	private static final String BASE_URL = "https://discord.com/api/oauth2";
	private static final String TOKEN_URL = BASE_URL + "/token";
	private static final String TOKEN_REVOKE_URL = BASE_URL + "/revoke";
	private static final String[] SCOPES = { "identify", "guilds" };
	private static final String GRANT_TYPE = "authorization_code";
	private static final String REDIRECT_URI = Config.discordapp_redirect_url;
	private static final String CLIENT_ID = Config.discordapp_client_id;
	private static final String CLIENT_SECRET = Config.discordapp_client_secret;

	public static final DiscordOAuth OAUTH_HANDLER = new DiscordOAuth(CLIENT_ID, CLIENT_SECRET, REDIRECT_URI, SCOPES);

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

	public static DiscordUser getUserWithToken(String access_token, @NonnullIf("Loading for the first time") String refresh_token) throws Exception {
		return users.get(access_token, () -> new DiscordUser(access_token, refresh_token, new DiscordAPI(access_token)));
	}
}