package com.github.black0nion.blackonionbot.config.immutable.api;

import com.github.black0nion.blackonionbot.config.common.exception.ConfigLoadingException;
import com.github.black0nion.blackonionbot.misc.enums.RunMode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Config {
	String getToken();

	String getDiscordappClientSecret();

	String getDiscordappClientId();

	String getDiscordappRedirectUrl();

	String getJdbcUrl();
	String getPostgresUsername();
	String getPostgresPassword();

	String getOpenWeatherMapApiKey();

	@Nonnull
	RunMode getRunMode();

	@Nullable
	String getTopggAuth();

	@Nullable
	String getSpotifyClientId();

	@Nullable
	String getSpotifyClientSecret();

	int getApiPort();

	long getVoteChannel();

	int getPrometheusPort();

	long getDevGuild();

	default void validate() throws ConfigLoadingException {
		// if any of the discordapp properties are null, throw an exception, but only if one of them is not null
		if (getDiscordappClientId() == null || getDiscordappClientSecret() == null || getDiscordappRedirectUrl() == null
				&& (getDiscordappClientId() != null || getDiscordappClientSecret() != null || getDiscordappRedirectUrl() != null)) {
			throw new ConfigLoadingException("A Discord App configuration is specified, but not all of them. Please specify all or none to continue.");
		}
	}
}