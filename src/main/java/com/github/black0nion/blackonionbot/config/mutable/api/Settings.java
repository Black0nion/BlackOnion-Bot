package com.github.black0nion.blackonionbot.config.mutable.api;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

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
