package com.github.black0nion.blackonionbot.config.api;

import com.github.black0nion.blackonionbot.config.BotMetadata;
import com.github.black0nion.blackonionbot.misc.RunMode;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Config {
	String getToken();

	Activity.ActivityType getActivityType();

	void setActivityType(Activity.ActivityType activityType);

	String getActivityName();

	void setActivityName(String activityName);

	OnlineStatus getOnlineStatus();

	void setOnlineStatus(OnlineStatus onlineStatus);

	String getActivityUrl();

	void setActivityUrl(String activityUrl);

	String getDiscordappClientSecret();

	String getDiscordappClientId();

	String getDiscordappRedirectUrl();

	String getMongoConnectionString();

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

	long getDevGuild();

	int getPrometheusPort();

	@Nullable
	String getLokiUrl();

	BotMetadata getMetadata();
}
