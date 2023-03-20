package com.github.black0nion.blackonionbot.config.mutable.impl;

import com.github.black0nion.blackonionbot.config.ConfigLoaderHolder;
import com.github.black0nion.blackonionbot.config.common.ConfigFlag;
import com.github.black0nion.blackonionbot.config.mutable.api.MutableConfigLoader;
import com.github.black0nion.blackonionbot.config.mutable.api.Settings;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import static com.github.black0nion.blackonionbot.config.common.Flags.*;

public class SettingsImpl extends ConfigLoaderHolder<MutableConfigLoader> implements Settings {

	private long logsChannel = get("logs_channel", Long.class, defaultValue(-1L), range(0, Long.MAX_VALUE));
	private Activity.ActivityType activityType = get("activity_type", Activity.ActivityType.class, defaultValue(Activity.ActivityType.LISTENING));
	private String activityName = get("activity_name", String.class, defaultValue("slashcommands"));
	private OnlineStatus onlineStatus = get("online_status", OnlineStatus.class, defaultValue(OnlineStatus.ONLINE));
	/**
	 * Currently unused, will get added by the slash command branch
	 */
	@SuppressWarnings("unused")
	private String activityUrl = get("activity_url", String.class, matchesRegex(Activity.STREAMING_URL));

	public SettingsImpl(MutableConfigLoader configLoader) {
		super(configLoader);
	}

	@Override
	public long getLogsChannel() {
		return logsChannel;
	}

	@Override
	public void setLogsChannel(long channel) {
		this.logsChannel = channel;
		set("logs_channel", channel);
	}

	public Activity.ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(Activity.ActivityType activityType) {
		this.activityType = activityType;
		set("activity_type", activityType);
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
		set("activity_name", activityName);
	}

	public OnlineStatus getOnlineStatus() {
		return onlineStatus;
	}

	public void setOnlineStatus(OnlineStatus onlineStatus) {
		this.onlineStatus = onlineStatus;
		set("online_status", onlineStatus);
	}

	public String getActivityUrl() {
		return activityUrl;
	}

	public void setActivityUrl(String activityUrl) {
		this.activityUrl = activityUrl;
		set("activity_url", activityUrl);
	}

	private void set(String name, Object value) {
		configLoader.set(name, value);
	}

	@SuppressWarnings("SameParameterValue")
	public <T> T get(String name, Class<T> clazz) {
		return configLoader.get(name, clazz);
	}

	public <T> T get(String name, Class<T> clazz, ConfigFlag... flagsArr) {
		return configLoader.get(name, clazz, flagsArr);
	}
}
