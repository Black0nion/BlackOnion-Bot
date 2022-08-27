package com.github.black0nion.blackonionbot.oauth;

import com.github.black0nion.blackonionbot.config.api.Config;
import io.mokulu.discord.oauth.DiscordOAuth;

import java.util.regex.Pattern;

/**
 * Wrapper around mokulu's discord oauth library.
 * @see DiscordOAuth
 */
public class OAuthAPI {

	private final String redirectUri;
	private final String clientId;
	private final String clientSecret;
	private final DiscordOAuth oAuthApi;

	private static OAuthAPI instance;

	public static OAuthAPI getInstance() {
		return instance;
	}

	public OAuthAPI(Config config) {
		instance = this;
		this.redirectUri = config.getDiscordappRedirectUrl();
		this.clientId = config.getDiscordappClientId();
		this.clientSecret = config.getDiscordappClientSecret();
		this.oAuthApi = new DiscordOAuth(clientId, clientSecret, redirectUri, SCOPES);
	}

	private static final String BASE_URL = "https://discord.com/api/oauth2";
	private static final String TOKEN_URL = BASE_URL + "/token";
	private static final String TOKEN_REVOKE_URL = BASE_URL + "/revoke";
	private static final String[] SCOPES = { "identify", "guilds" };
	private static final String GRANT_TYPE = "authorization_code";

	public static final Pattern TOKEN_PATTERN = Pattern.compile("[a-zA-Z\\d]{30,32}");

	public String getAuthorizeURL() {
		return oAuthApi.getAuthorizationURL(null);
	}

	public String getTokenURL(String code) {
		return TOKEN_URL + "?client_id=" + clientId + "&client_secret=" + clientSecret + "&grant_type=" + GRANT_TYPE + "&code=" + code + "&redirect_uri=" + redirectUri;
	}

	public String getTokenRevokeURL(String token) {
		return TOKEN_REVOKE_URL + "?client_id=" + clientId + "&client_secret=" + clientSecret + "&token=" + token;
	}

	public DiscordOAuth getOAuthApi() {
		return oAuthApi;
	}
}
