package com.github.black0nion.blackonionbot.utils.config;

import com.github.black0nion.blackonionbot.misc.RunMode;
import com.google.gson.internal.Primitives;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.github.black0nion.blackonionbot.utils.Utils.gOD;
import static com.github.black0nion.blackonionbot.utils.config.Flag.NONNULL;

public class Config {

	public static final String token = get("token", String.class, NONNULL);
	public static final String prefix = gOD(get("prefix", String.class), "*");
	public static Activity.ActivityType activity_type = gOD(get("activity_type", Activity.ActivityType.class), Activity.ActivityType.DEFAULT);
	public static String activity_name = gOD(get("activity_name", String.class), prefix);
	public static OnlineStatus online_status = gOD(get("online_status", OnlineStatus.class), OnlineStatus.ONLINE);
	/**
	 * Currently unused, will get added by the slash command branch
	 */
	@SuppressWarnings("unused")
	public static final String activity_url = get("activity_url", String.class);
	public static final String discordapp_client_secret = get("discordapp_client_secret", String.class);
	public static final String discordapp_client_id = get("discordapp_client_id", String.class);
	public static final String discordapp_redirect_url = get("discordapp_redirect_url", String.class);
	public static final String mongo_connection_string = get("mongo_connection_string", String.class, NONNULL);
	public static final int mongo_timeout = gOD(get("mongo_timeout", Integer.class), 30000);
	@Nullable
	public static final String influx_database_url = get("influx_database_url", String.class);
	@Nullable
	public static final String influx_token = get("influx_token", String.class);
	@Nullable
	public static final String influx_org = get("influx_org", String.class);
	@Nonnull
	public static final RunMode run_mode = gOD(get("run_mode", RunMode.class), RunMode.DEV);
	@Nullable
	public static final String topgg_auth = get("topgg_auth", String.class);

	@Nullable
	public static final String content_moderator_token = get("content_moderator_token", String.class);
	@Nullable
	public static final String spotify_client_id = get("spotify_client_id", String.class);
	@Nullable
	public static final String spotify_client_secret = get("spotify_client_secret", String.class);
	public static final int api_port = gOD(get("api_port", Integer.class), 187);
	public static final boolean log_heartbeats = gOD(get("log_heartbeats", Boolean.class), false);
	public static final long vote_channel = gOD(get("vote_channel", Long.class), -1L);

	private static <T> T get(String name, Class<T> clazz) {
		return get(name, clazz, 0);
	}

	@SuppressWarnings("unchecked")
	private static <T> T get(String name, Class<T> clazz, int flags) {
		final String value = System.getProperty(name);
		if (value == null) {
			if ((flags & NONNULL) != 0) {
				throw new IllegalArgumentException("Missing required config value: " + name);
			}
			return null;
		}
		T result;
		if (clazz.equals(String.class)) {
			result = (T) value;
		} else if (clazz.equals(Integer.class)) {
			result = (T) Integer.valueOf(value);
		} else if (clazz.equals(Long.class)) {
			result = (T) Long.valueOf(value);
		} else if (clazz.equals(Boolean.class)) {
			result = (T) Boolean.valueOf(value);
		} else if (clazz.equals(Double.class)) {
			result = (T) Double.valueOf(value);
		} else if (clazz.equals(Float.class)) {
			result = (T) Float.valueOf(value);
		} else {
			result = Primitives.wrap(clazz).cast(value);
		}
		return result;
	}
}