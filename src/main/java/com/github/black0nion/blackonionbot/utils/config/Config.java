package com.github.black0nion.blackonionbot.utils.config;

import com.github.black0nion.blackonionbot.misc.RunMode;
import com.google.gson.internal.Primitives;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.github.black0nion.blackonionbot.utils.config.Flags.*;

// the nullable things are already checked by the get value so the warnings can be ignored
@SuppressWarnings("ConstantConditions")
public class Config {
	private static Config instance;
	private Config() {
		instance = this;
	}

	/**
	 * Creates a new config object if {@link Config#instance} is null.
	 */
	public static Config getInstance() {
		// enable logback to preload the config
		if (instance == null) {
			try {
				ConfigManager.loadConfig();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			new Config();
		}
		return instance;
	}

	private final String token = get("token", String.class, NonNull, matchesRegex("[MN][A-Za-z\\d]{23}\\.[\\w-]{6}\\.[\\w-]{27}"));
	private final @Nonnull String prefix = get("prefix", String.class, defaultValue("*"), matchesRegex("\\w{1,10}"));
	private Activity.ActivityType activityType = get("activity_type", Activity.ActivityType.class, defaultValue(Activity.ActivityType.PLAYING));
	private String activityName = get("activity_name", String.class, defaultValue(prefix));
	private OnlineStatus onlineStatus = get("online_status", OnlineStatus.class, defaultValue(OnlineStatus.ONLINE));
	/**
	 * Currently unused, will get added by the slash command branch
	 */
	@SuppressWarnings("unused")
	private String activityUrl = get("activity_url", String.class, matchesRegex(Activity.STREAMING_URL));
	private final String discordappClientSecret = get("discordapp_client_secret", String.class, matchesRegex(Pattern.compile("^[a-z\\d=_\\-]{32}$", Pattern.CASE_INSENSITIVE)));
	private final String discordappClientId = get("discordapp_client_id", String.class, matchesRegex("\\d{17,19}"));
	private final String discordappRedirectUrl = get("discordapp_redirect_url", String.class, matchesRegex("https?://.+"));
	private final String mongoConnectionString = get("mongo_connection_string", String.class, Flags.NonNull, matchesRegex("^mongodb:\\/\\/(?:(?:(\\w+)?:(\\w+)?@)|:?@?)((?:[\\w.-])+)(?::(\\d+))?(?:\\/([\\w-]+)?)?(?:\\?([\\w-]+=[\\w-]+(?:&[\\w-]+=[\\w-]+)*)?)?$"));
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
	private static final BotMetadata metadata = ConfigManager.metadata;
	@Nullable
	private final String lokiUrl = get("loki_url", String.class);

	//region Getters and Setters
	public String getToken() {
		return token;
	}

	@Nonnull
	public String getPrefix() {
		return prefix;
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

	public int getPrometheusPort() {
		return prometheusPort;
	}

	@Nullable
	public String getLokiUrl() {
		return lokiUrl;
	}

	public BotMetadata getMetadata() {
		return metadata;
	}
	//endregion

	@SuppressWarnings("SameParameterValue")
	public static <T> T get(String name, Class<T> clazz) {
		return get(name, clazz, new IFlag[0]);
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(String name, Class<T> clazz, IFlag... flagsArr) {
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
			if (f instanceof Flags.MatchesRegex flag && !flag.regex().matcher(value).matches()) {
				throw new IllegalArgumentException("Config value " + name + " does not match regex " + flag.regex());
			} else if (f instanceof Flags.Range flag
					&& result instanceof Number num
					&& (num.doubleValue() < flag.min() || num.doubleValue() > flag.max())) {
				throw new IllegalArgumentException("Config value " + name + " is out of range " + flag.min() + " to " + flag.max());
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
