package com.github.black0nion.blackonionbot.config.immutable.api;

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
}
