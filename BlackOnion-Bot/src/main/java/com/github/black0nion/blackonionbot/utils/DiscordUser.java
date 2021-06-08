package com.github.black0nion.blackonionbot.utils;

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
	return userId;
    }

    public String getUserName() {
	return userName;
    }

    public String getAvatar() {
	return avatar;
    }

    public String getDiscriminator() {
	return discriminator;
    }

    public String getLocale() {
	return locale;
    }

    public boolean isMfaEnabled() {
	return mfa_enabled;
    }
}
