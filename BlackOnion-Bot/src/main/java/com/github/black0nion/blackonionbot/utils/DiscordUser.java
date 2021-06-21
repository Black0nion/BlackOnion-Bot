package com.github.black0nion.blackonionbot.utils;

import org.json.JSONObject;

public class DiscordUser {

    final long userId;
    final String userName;
    final String avatar;
    final String discriminator;
    final String locale;
    final boolean mfa_enabled;

    public DiscordUser(final long userId, final String userName, final String avatar, final String discriminator, final String locale, final boolean mfa_enabled) {
	this.userId = userId;
	this.userName = userName;
	this.avatar = avatar;
	this.discriminator = discriminator;
	this.locale = locale;
	this.mfa_enabled = mfa_enabled;
    }

    public long getUserId() {
	return this.userId;
    }

    public String getUserName() {
	return this.userName;
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
	return "DiscordUser [userId=" + this.userId + ", userName=" + this.userName + ", avatar=" + this.avatar + ", discriminator=" + this.discriminator + ", locale=" + this.locale + ", mfa_enabled=" + this.mfa_enabled + "]";
    }

    public JSONObject toJsonObject() {
	return new JSONObject().put("userid", this.userId).put("username", this.userName).put("avatar", this.avatar).put("discriminator", this.discriminator).put("locale", this.locale).put("mfa_enabled", this.mfa_enabled);
    }
}