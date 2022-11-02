package com.github.black0nion.blackonionbot.config.mutable.api;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

/**
 * This interface represents the settings of the bot.
 * The difference to {@link com.github.black0nion.blackonionbot.config.immutable.api.Config} is that the settings defined here can be changed at runtime.
 */
@SuppressWarnings("CheckStyle")
public interface Settings {

	long getLogsChannel();
	void setLogsChannel(long channel);

	Activity.ActivityType getActivityType();
	void setActivityType(Activity.ActivityType activityType);

	String getActivityName();
	void setActivityName(String activityName);

	OnlineStatus getOnlineStatus();
	void setOnlineStatus(OnlineStatus onlineStatus);

	String getActivityUrl();
	void setActivityUrl(String activityUrl);
}
