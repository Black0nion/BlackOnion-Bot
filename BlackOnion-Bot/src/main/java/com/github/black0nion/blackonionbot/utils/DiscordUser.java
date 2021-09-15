package com.github.black0nion.blackonionbot.utils;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mashape.unirest.http.Unirest;

public class DiscordUser {

    private final long id;
    private final String username;
    private final String avatar;
    private final String discriminator;
    private final String locale;
    private final boolean mfa_enabled;
    private String accessToken;
    private JSONArray guilds = null;

    public DiscordUser(final String accessToken, final long userId, final String userName, final String avatar, final String discriminator, final String locale, final boolean mfa_enabled) {
	this.accessToken = accessToken;
	this.id = userId;
	this.username = userName;
	this.avatar = avatar;
	this.discriminator = discriminator;
	this.locale = locale;
	this.mfa_enabled = mfa_enabled;
    }

    @Deprecated
    public DiscordUser(final long userId, final String userName, final String avatar, final String discriminator, final String locale, final boolean mfa_enabled) {
	this.accessToken = null;
	this.id = userId;
	this.username = userName;
	this.avatar = avatar;
	this.discriminator = discriminator;
	this.locale = locale;
	this.mfa_enabled = mfa_enabled;
    }

    public long getUserId() {
	return this.id;
    }

    public String getUserName() {
	return this.username;
    }

    /**
     * @return the users full name in Username#Tag format
     */
    public String getFullName() {
	return this.getUserName() + "#" + this.getDiscriminator();
    }

    public String getAvatar() {
	return this.avatar;
    }

    public String getDiscriminator() {
	return this.discriminator;
    }

    public String getLocale() {
	return this.locale;
    }

    public boolean isMfaEnabled() {
	return this.mfa_enabled;
    }

    @Override
    public String toString() {
	return "DiscordUser [userId=" + this.id + ", userName=" + this.username + ", avatar=" + this.avatar + ", discriminator=" + this.discriminator + ", locale=" + this.locale + ", mfa_enabled=" + this.mfa_enabled + "]";
    }

    public JSONObject toJsonObject() {
	return new JSONObject().put("userid", this.id).put("username", this.username).put("avatar", this.avatar).put("discriminator", this.discriminator).put("locale", this.locale).put("mfa_enabled", this.mfa_enabled);
    }

    private final Supplier<JSONArray> reloadSupplier = Suppliers.memoizeWithExpiration(() -> {
	try {
	    Unirest.setTimeouts(0, 0);
	    final String body = Unirest.get("https://discord.com/api/users/@me/guilds").header("Authorization", "Bearer " + DiscordUser.this.accessToken).asString().getBody();
	    return new JSONArray(body);
	} catch (final Exception e) {
	    return null;
	}
    }, 10, TimeUnit.MINUTES);

    /**
     * @return the guilds as discord's json response or null
     */
    @Nullable
    public JSONArray getGuilds() {
	try {
	    this.guilds = this.reloadSupplier.get();
	    return this.guilds;
	} catch (final Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }

    public DiscordUser setToken(final String token) {
	this.accessToken = token;
	return this;
    }
}