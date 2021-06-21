package com.github.black0nion.blackonionbot.API;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.utils.CredentialsManager;
import com.github.black0nion.blackonionbot.utils.DiscordUser;
import com.github.black0nion.blackonionbot.utils.Trio;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

public class OAuthUtils {

    private static final LoadingCache<String, DiscordUser> cachedUsers = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build(new CacheLoader<String, DiscordUser>() {
	@Override
	public DiscordUser load(final String token) {
	    return loadUserFromToken(token);
	}
    });

    /**
     * @param code The code from Discord
     * @return a trio with the values: AccessToken | RefreshToken | ExpiresIn
     */
    @Nullable
    public static Trio<String, String, Integer> getTokensFromCode(final String code) {
	try {
	    Unirest.setTimeouts(0, 0);
	    final CredentialsManager manager = Bot.getCredentialsManager();
	    final HttpResponse<String> responseRaw = Unirest.post("https://discord.com/api/v9/oauth2/token").header("Content-Type", "application/x-www-form-urlencoded").field("client_id", manager.getString("client_id")).field("client_secret", manager.getString("client_secret")).field("grant_type", "authorization_code").field("code", code).field("redirect_uri", manager.getString("redirect_uri")).asString();
	    final JSONObject response = new JSONObject(responseRaw.getBody());
	    if (response.has("access_token") && response.has("refresh_token")) return new Trio<>(response.getString("access_token"), response.getString("refresh_token"), response.getInt("expires_in"));
	} catch (final Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    @Nullable
    public static JSONObject getUserInfoFromToken(final String accessToken) {
	try {
	    Unirest.setTimeouts(0, 0);
	    final HttpResponse<String> response = Unirest.get("https://discord.com/api/v9/users/@me").header("Authorization", "Bearer " + accessToken).asString();
	    final JSONObject result = new JSONObject(response.getBody());
	    if (!result.has("id")) return null;
	    else return result;
	} catch (final Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    @Nullable
    public static DiscordUser getUserFromToken(final String accessToken) {
	try {
	    return cachedUsers.get(accessToken);
	} catch (final Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }

    @Nullable
    private static DiscordUser loadUserFromToken(final String accessToken) {
	final JSONObject userinfo = getUserInfoFromToken(accessToken);
	if (userinfo == null) return null;
	return new DiscordUser(userinfo.getLong("id"), userinfo.getString("username"), userinfo.getString("avatar"), userinfo.getString("discriminator"), userinfo.getString("locale"), userinfo.getBoolean("mfa_enabled"));
    }
}