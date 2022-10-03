package com.github.black0nion.blackonionbot.config.impl;

import com.github.black0nion.blackonionbot.config.*;
import com.github.black0nion.blackonionbot.config.api.ConfigLoader;
import com.github.black0nion.blackonionbot.misc.RunMode;
import com.github.black0nion.blackonionbot.config.api.Config;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Pattern;

import static com.github.black0nion.blackonionbot.config.Flags.*;

// the nullable things are already checked by the get value so the warnings can be ignored
public class ConfigImpl extends ConfigWithConfigLoader implements Config {

	public ConfigImpl(ConfigLoader configLoader) {
		super(configLoader);
	}

	private final String token = get("token", String.class, NonNull, matchesRegex("^[A-Za-z\\d]{24}.[\\w-]{6}.[\\w-]{26,40}$"));
	private Activity.ActivityType activityType = get("activity_type", Activity.ActivityType.class, defaultValue(Activity.ActivityType.LISTENING));
	private String activityName = get("activity_name", String.class, defaultValue("slashcommands"));
	private OnlineStatus onlineStatus = get("online_status", OnlineStatus.class, defaultValue(OnlineStatus.ONLINE));
	/**
	 * Currently unused, will get added by the slash command branch
	 */
	@SuppressWarnings("unused")
	private String activityUrl = get("activity_url", String.class, matchesRegex(Activity.STREAMING_URL));
	private final String discordappClientSecret = get("discordapp_client_secret", String.class, matchesRegex(Pattern.compile("^[a-z\\d=_\\-]{32}$", Pattern.CASE_INSENSITIVE)));
	private final String discordappClientId = get("discordapp_client_id", String.class, matchesRegex("\\d{17,19}"));
	private final String discordappRedirectUrl = get("discordapp_redirect_url", String.class, matchesRegex("https?://.+"));
	private final String mongoConnectionString = get("mongo_connection_string", String.class, NonNull, matchesRegex("^mongodb(\\+srv)?:\\/\\/(?:(?:(\\w+)?:(\\w+)?@)|:?@?)((?:[\\w.-])+)(?::(\\d+))?(?:\\/([\\w-]+)?)?(?:\\?([\\w-]+=[\\w-]+(?:&[\\w-]+=[\\w-]+)*)?)?$"));
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
	private long logsChannel = get("logs_channel", Long.class, defaultValue(-1), range(0, Long.MAX_VALUE));

	//region Getters and Setters
	public String getToken() {
		return token;
	}

	public Activity.ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(Activity.ActivityType activityType) {
		this.activityType = activityType;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public OnlineStatus getOnlineStatus() {
		return onlineStatus;
	}

	public void setOnlineStatus(OnlineStatus onlineStatus) {
		this.onlineStatus = onlineStatus;
	}

	public String getActivityUrl() {
		return activityUrl;
	}

	public void setActivityUrl(String activityUrl) {
		this.activityUrl = activityUrl;
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

	public String getMongoConnectionString() {
		return mongoConnectionString;
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

	@Override
	public long getLogsChannel() {
		return logsChannel;
	}

	@Override
	public void setLogsChannel(long channel) {
		this.logsChannel = channel;
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
