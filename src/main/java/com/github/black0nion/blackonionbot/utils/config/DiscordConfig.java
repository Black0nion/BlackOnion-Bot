package com.github.black0nion.blackonionbot.utils.config;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DiscordConfig {
	@Nonnull
	public final String TOKEN = "null";
	@Nullable
	public String DEFAULT_PREFIX = null;
	@Nullable
	public Activity.ActivityType ACTIVITY_TYPE = null;
	@Nullable
	public String ACTIVITY = null;

	/**
	 * Currently unused, will get added by the slash command branch
	 */
	@Nullable
    public String ACTIVITY_URL = null;

	@Nullable
	public OnlineStatus STATUS = null;

	@Nullable
	public final DashboardApplication DASHBOARD_APPLICATION = new DashboardApplication();

	public final long VOTE_CHANNEL = -1;

	@Override
	public String toString() {
		return "DiscordConfig{" +
				"TOKEN={REDACTED, " + TOKEN + (!TOKEN.isEmpty() ? TOKEN.length() : "null") + "}" +
				", DEFAULT_PREFIX='" + DEFAULT_PREFIX + '\'' +
				", ACTIVITY_TYPE=" + ACTIVITY_TYPE +
				", ACTIVITY='" + ACTIVITY + '\'' +
				", ACTIVITY_URL='" + ACTIVITY_URL + '\'' +
				", DASHBOARD_APPLICATION=" + DASHBOARD_APPLICATION +
				", VOTE_CHANNEL=" + VOTE_CHANNEL +
				'}';
	}
}