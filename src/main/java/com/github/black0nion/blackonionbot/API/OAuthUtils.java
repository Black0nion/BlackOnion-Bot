package com.github.black0nion.blackonionbot.API;

import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.utils.DiscordUser;
import com.github.black0nion.blackonionbot.utils.Trio;
import static com.github.black0nion.blackonionbot.utils.config.Config.*;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheLoader.InvalidCacheLoadException;
import com.google.common.cache.LoadingCache;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mongodb.client.model.Filters;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

public class OAuthUtils {

    private static final LoadingCache<String, DiscordUser> cachedUsers = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build(new CacheLoader<>() {
		@Override
		public @NotNull DiscordUser load(@NotNull String key) throws NullPointerException {
			DiscordUser discordUser = loadUserFromToken(key);
			if (discordUser == null) throw new NullPointerException("DiscordUser is null");
			return discordUser;
		}
	});

    /**
     * @param  code The code from Discord
     * @return      a trio with the values: AccessToken | RefreshToken | ExpiresIn
     */
    @Nullable
    public static Trio<String, String, Integer> getTokensFromCode(final String code) {
	try {
	    LogOrigin.API.info("Getting a token from code...");
		if (discordapp_redirect_url == null || discordapp_client_secret == null || discordapp_client_id == null) {
			LogOrigin.API.error("Discord client ID, client secret or redirect URI is null!");
		}
	    Unirest.setTimeouts(0, 0);
	    final HttpResponse<String> responseRaw = Unirest.post("https://discord.com/api/v9/oauth2/token")
				.header("Content-Type", "application/x-www-form-urlencoded")
				.field("client_id", discordapp_client_id)
				.field("client_secret", discordapp_client_secret)
				.field("grant_type", "authorization_code")
				.field("code", code)
				.field("redirect_uri", discordapp_redirect_url)
			.asString();
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
	    if (e instanceof NullPointerException) return null;
	    if (e instanceof InvalidCacheLoadException) {
		BlackSession.collection.deleteOne(Filters.eq("access_token", accessToken));
		return null;
	    }
	    e.printStackTrace();
	    return null;
	}
    }

    @Nullable
    private static DiscordUser loadUserFromToken(final String accessToken) {
	final JSONObject userinfo = getUserInfoFromToken(accessToken);
	if (userinfo == null) return null;
	final DiscordUser discordUser = new DiscordUser(accessToken, userinfo.getLong("id"), userinfo.getString("username"), userinfo.getString("avatar"), userinfo.getString("discriminator"), userinfo.getString("locale"), userinfo.getBoolean("mfa_enabled"));
	LogOrigin.API.info("Loaded user " + discordUser.getFullName() + "!");
	return discordUser;
    }
}