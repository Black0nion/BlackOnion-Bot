package com.github.black0nion.blackonionbot.utils.config;

import com.github.black0nion.blackonionbot.misc.RunMode;
import com.google.gson.internal.Primitives;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.github.black0nion.blackonionbot.utils.config.Flags.*;

@SuppressWarnings("ConstantConditions")
public class Config {

	public static final String token = get("token", String.class, NonNull, matchesRegex("[MN][A-Za-z\\d]{23}\\.[\\w-]{6}\\.[\\w-]{27}"));
	public static final @Nonnull String prefix = get("prefix", String.class, defaultValue("*"), matchesRegex("\\w{1,10}"));
	public static Activity.ActivityType activity_type = get("activity_type", Activity.ActivityType.class, defaultValue(Activity.ActivityType.PLAYING));
	public static String activity_name = get("activity_name", String.class, defaultValue(prefix));
	public static OnlineStatus online_status = get("online_status", OnlineStatus.class, defaultValue(OnlineStatus.ONLINE));
	/**
	 * Currently unused, will get added by the slash command branch
	 */
	@SuppressWarnings("unused")
	public static final String activity_url = get("activity_url", String.class, matchesRegex(Activity.STREAMING_URL));
	public static final String discordapp_client_secret = get("discordapp_client_secret", String.class, matchesRegex(Pattern.compile("^[a-z\\d=_\\-]{32}$", Pattern.CASE_INSENSITIVE)));
	public static final String discordapp_client_id = get("discordapp_client_id", String.class, matchesRegex("\\d{17,19}"));
	public static final String discordapp_redirect_url = get("discordapp_redirect_url", String.class, matchesRegex("https?://.+"));
	public static final String mongo_connection_string = get("mongo_connection_string", String.class, Flags.NonNull, matchesRegex("^mongodb:\\/\\/(?:(?:(\\w+)?:(\\w+)?@)|:?@?)((?:[\\w.-])+)(?::(\\d+))?(?:\\/([\\w-]+)?)?(?:\\?([\\w-]+=[\\w-]+(?:&[\\w-]+=[\\w-]+)*)?)?$"));
	@Nullable
	public static final String influx_database_url = get("influx_database_url", String.class);
	@Nullable
	public static final String influx_token = get("influx_token", String.class);
	@Nullable
	public static final String influx_org = get("influx_org", String.class);
	public static final String openweatherapikey = get("openweathermap_api_key", String.class, matchesRegex("[a-z\\d]{32}"));
	@Nonnull
	public static final RunMode run_mode = get("run_mode", RunMode.class, defaultValue(RunMode.DEV));
	@Nullable
	public static final String topgg_auth = get("topgg_auth", String.class);

	@Nullable
	public static final String content_moderator_token = get("content_moderator_token", String.class);
	@Nullable
	public static final String spotify_client_id = get("spotify_client_id", String.class, matchesRegex("[\\w\\d]{32}"));
	@Nullable
	public static final String spotify_client_secret = get("spotify_client_secret", String.class, matchesRegex("[\\w\\d]{32}"));
	public static final int api_port = get("api_port", Integer.class, defaultValue(187), range(0, 65535));
	public static final boolean log_heartbeats = get("log_heartbeats", Boolean.class, defaultValue(false));
	public static final long vote_channel = get("vote_channel", Long.class, defaultValue(-1L));
	public static final long dev_guild = get("dev_guild", Long.class, defaultValue(-1L));
	public static final BotMetadata metadata = ConfigManager.metadata;

	@SuppressWarnings("SameParameterValue")
	private static <T> T get(String name, Class<T> clazz) {
		return get(name, clazz, new IFlag[0]);
	}

	@SuppressWarnings("unchecked")
	static <T> T get(String name, Class<T> clazz, IFlag... flagsArr) {
		name = name.toUpperCase(Locale.ROOT);
		final String value = System.getenv().containsKey(name) ? System.getenv(name) : System.getProperty(name);
		List<IFlag> flags = List.of(flagsArr);
		if (value == null) {
			if (flags.contains(Flags.NonNull)) {
				throw new IllegalArgumentException("Missing required config value: " + name);
			}
			Flags.Default<T> defaultFlag = getFlag(flags, Flags.Default.class);
			return defaultFlag != null ? defaultFlag.defaultValue() : null;
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
		for (IFlag f : flags) {
			if (f instanceof Flags.MatchesRegex flag) {
				if (!flag.regex().matcher(value).matches()) {
					throw new IllegalArgumentException("Config value " + name + " does not match regex " + flag.regex());
				}
			} else if (f instanceof Flags.Range flag) {
				if (result instanceof Number) {
					if (((Number) result).doubleValue() < flag.min() || ((Number) result).doubleValue() > flag.max()) {
						throw new IllegalArgumentException("Config value " + name + " is out of range " + flag.min() + " to " + flag.max());
					}
				}
			}
		}
		return result;
	}

	private static <T extends IFlag> T getFlag(List<IFlag> flags, @SuppressWarnings("SameParameterValue") Class<T> clazz) {
		return flags.stream()
			.filter(Objects::nonNull)
			.filter(f -> clazz.isAssignableFrom(f.getClass()))
			.map(clazz::cast)
			.findFirst()
			.orElse(null);
	}
}