package com.github.black0nion.blackonionbot.config.immutable.impl;

import com.github.black0nion.blackonionbot.config.ConfigLoaderHolder;
import com.github.black0nion.blackonionbot.config.common.ConfigFlag;
import com.github.black0nion.blackonionbot.config.common.ConfigLoader;
import com.github.black0nion.blackonionbot.config.immutable.api.Config;
import com.github.black0nion.blackonionbot.misc.enums.RunMode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Pattern;

import static com.github.black0nion.blackonionbot.config.common.Flags.*;

// the nullable things are already checked by the get value so the warnings can be ignored
public class ConfigImpl extends ConfigLoaderHolder<ConfigLoader> implements Config {

	public ConfigImpl(ConfigLoader configLoader) {
		super(configLoader);
		validate();
	}

	private final String token = get("token", String.class, NonNull, matchesRegex("^[A-Za-z\\d]{24}.[\\w-]{6}.[\\w-]{26,40}$"));
	private final String discordappClientSecret = get("discordapp_client_secret", String.class, matchesRegex(Pattern.compile("^[a-z\\d=_\\-]{32}$", Pattern.CASE_INSENSITIVE)));
	private final String discordappClientId = get("discordapp_client_id", String.class, matchesRegex("\\d{17,19}"));
	private final String discordappRedirectUrl = get("discordapp_redirect_url", String.class, matchesRegex("https?://.+"));

	private final String jdbcUrl = get("jdbc_url", String.class, NonNull, matchesRegex("jdbc:postgresql://.+"));
	private final String postgresUsername = get("postgres_username", String.class, NonNull);
	private final String postgresPassword = get("postgres_password", String.class, NonNull);

	private final String openWeatherMapApiKey = get("openweathermap_api_key", String.class, matchesRegex("[a-z\\d]{32}"));
	@Nonnull
	private final RunMode runMode = get("run_mode", RunMode.class, defaultValue(RunMode.DEV));
	@Nullable
	private final String topggAuth = get("topgg_auth", String.class);

	@Nullable
	private final String spotifyClientId = get("spotify_client_id", String.class, matchesRegex("[\\w\\d]{32}"));
	@Nullable
	private final String spotifyClientSecret = get("spotify_client_secret", String.class, matchesRegex("[\\w\\d]{32}"));
	private final int apiPort = get("api_port", Integer.class, defaultValue(187), range(0, 65535));
	private final long voteChannel = get("vote_channel", Long.class, defaultValue(-1L));
	private final long devGuild = get("dev_guild", Long.class, defaultValue(-1L));
	private final int prometheusPort = get("prometheus_port", Integer.class, defaultValue(9090), range(0, 65535));

	//region Getters and Setters
	public String getToken() {
		return token;
	}

	public String getDiscordappClientSecret() {
		return discordappClientSecret;
	}

	public String getDiscordappClientId() {
		return discordappClientId;
	}

	public String getDiscordappRedirectUrl() {
		return discordappRedirectUrl;
	}

	@Override
	public String getJdbcUrl() {
		return jdbcUrl;
	}

	@Override
	public String getPostgresUsername() {
		return postgresUsername;
	}

	@Override
	public String getPostgresPassword() {
		return postgresPassword;
	}

	public String getOpenWeatherMapApiKey() {
		return openWeatherMapApiKey;
	}

	@Nonnull
	public RunMode getRunMode() {
		return runMode;
	}

	@Nullable
	public String getTopggAuth() {
		return topggAuth;
	}

	@Nullable
	public String getSpotifyClientId() {
		return spotifyClientId;
	}

	@Nullable
	public String getSpotifyClientSecret() {
		return spotifyClientSecret;
	}

	public int getApiPort() {
		return apiPort;
	}

	public long getVoteChannel() {
		return voteChannel;
	}

	public long getDevGuild() {
		return devGuild;
	}

	public int getPrometheusPort() {
		return prometheusPort;
	}

	//endregion

	@SuppressWarnings("SameParameterValue")
	public <T> T get(String name, Class<T> clazz) {
		return configLoader.get(name, clazz);
	}

	public <T> T get(String name, Class<T> clazz, ConfigFlag... flagsArr) {
		return configLoader.get(name, clazz, flagsArr);
	}
}