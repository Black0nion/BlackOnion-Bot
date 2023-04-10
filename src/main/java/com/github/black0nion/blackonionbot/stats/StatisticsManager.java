package com.github.black0nion.blackonionbot.stats;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.config.featureflags.FeatureFlags;
import com.github.black0nion.blackonionbot.config.immutable.BotMetadata;
import com.github.black0nion.blackonionbot.config.immutable.ConfigFileLoader;
import com.github.black0nion.blackonionbot.config.immutable.api.Config;
import com.github.black0nion.blackonionbot.utils.Utils;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.Interaction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StatisticsManager extends ListenerAdapter {

	private static final String GUILD_ID = "guild_id";
	private static final String GUILD = "guild";
	private static final String CHANNEL_ID = "channel_id";
	private static final String CHANNEL = "channel";

	public static final long STARTUP_TIME = System.currentTimeMillis();
	private static final String NAMESPACE = "blackonionbot";
	/** in seconds */
	public static final int DELAY_BETWEEN_COLLECTION = 15;

	private static final Logger logger = LoggerFactory.getLogger(StatisticsManager.class);

	private final FeatureFlags featureFlags;

	public StatisticsManager(Config config, FeatureFlags featureFlags) {
		this.featureFlags = featureFlags;
		final BotMetadata metadata = ConfigFileLoader.getMetadata();
		Gauge.build()
			.name("info")
			.help("Build information")
			.namespace(NAMESPACE)
			.labelNames("run_mode", "version", "lines_of_code", "files")
			.register()
			.labels(
				config.getRunMode().name(),
				metadata.version(),
				String.valueOf(metadata.lines_of_code()),
				String.valueOf(metadata.files()))
			.set(1);
	}

	public void start() {
		ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor(); // NOSONAR closing it will shut it down
		ex.scheduleAtFixedRate(new StatsJob(featureFlags), 0, DELAY_BETWEEN_COLLECTION, TimeUnit.SECONDS);
	}

	public static final Gauge UPTIME = Gauge.build()
		.name("uptime")
		.help("How long the bot has been running")
		.unit("seconds")
		.namespace(NAMESPACE)
		.register();

	public static final Counter COMMANDS_EXECUTED = Counter.build()
		.name("commands_executed")
		.help("Total number of commands executed")
		.namespace(NAMESPACE)
		.labelNames("type", "command", GUILD_ID, GUILD, CHANNEL_ID, CHANNEL)
		.register();

	public static final Counter TOTAL_COMMANDS_EXECUTED = Counter.build()
		.name("total_commands_executed")
		.help("Total number of commands executed")
		.namespace(NAMESPACE)
		.create();

	public static final Counter MESSAGES_SENT = Counter.build()
		.name("messages_sent")
		.help("Total number of messages sent")
		.namespace(NAMESPACE)
		.labelNames(GUILD_ID, GUILD, CHANNEL_ID, CHANNEL)
		.register();

	/**
	 * Only required for internal use.
	 */
	public static final Counter TOTAL_MESSAGES_SENT = Counter.build()
		.name("total_messages_sent")
		.help("Total number of messages sent")
		.namespace(NAMESPACE)
		.create();

	static final Gauge RAM_LOAD = Gauge.build()
		.name("ram_load")
		.help("Total amount of RAM used")
		.namespace(NAMESPACE)
		.labelNames("max_ram")
		.unit("bytes")
		.register();

	static final Gauge CPU_LOAD = Gauge.build()
		.name("cpu_load")
		.help("Total amount of CPU used")
		.namespace(NAMESPACE)
		.unit("ratio")
		.register();

	private static final Counter EVENTS = Counter.build()
		.name("events")
		.help("Events received")
		.namespace(NAMESPACE)
		.labelNames("type", GUILD_ID, GUILD)
		.register();

	static final Gauge GUILD_COUNT = Gauge.build()
		.name("guild_count")
		.help("Total number of guilds")
		.namespace(NAMESPACE)
		.register();

	static final Gauge USER_COUNT = Gauge.build()
		.name("user_count")
		.help("Total number of users")
		.namespace(NAMESPACE)
		.register();

	static final Gauge PING = Gauge.build()
		.name("ping")
		.help("The gateway ping of the bot")
		.namespace(NAMESPACE)
		.register();

	public static double getProcessRamLoad() {
		final Runtime runtime = Runtime.getRuntime();
		return runtime.totalMemory() - runtime.freeMemory();
	}

	public static double getProcessMaxRamLoad() {
		final Runtime runtime = Runtime.getRuntime();
		return runtime.maxMemory();
	}

	private static int guildCount = -1;

	static int reloadGuildCount() {
		guildCount = (int) Bot.getInstance().getJDA().getGuildCache().size();
		return guildCount;
	}

	public static int getGuildCount() {
		return guildCount;
	}

	private static long userCount = -1;

	static long reloadUserCount() {
		userCount = Bot.getInstance().getJDA().getGuildCache().stream().map(Guild::getMemberCount).mapToInt(Integer::intValue).sum();
		return userCount;
	}

	public static long getUserCount() {
		return userCount;
	}

	public static long getGatewayPing() {
		// JDA's method returns a double
		return (long) PING.get();
	}

	public static double getProcessCpuLoad() {
		try {
			final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			final ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
			final AttributeList list = mbs.getAttributes(name, new String[]{"ProcessCpuLoad"});

			if (list.isEmpty()) return Double.NaN;

			final Attribute att = (Attribute) list.get(0);
			final Double value = (Double) att.getValue();

			// usually takes a couple of seconds before we get real values
			if (value == -1.0) return Double.NaN;
			// returns a percentage value with 1 decimal point precision
			return Utils.roundToDouble("#0.000", value);
		} catch (final Exception e) {
			logger.error("Error while getting CPU load", e);
		}
		return Double.NaN;
	}

	@Override
	public void onGenericEvent(@NotNull GenericEvent event) {
		String guildId = "none";
		String guildName = "none";
		if (event instanceof GenericGuildEvent guildEvent) {
			guildId = guildEvent.getGuild().getId();
			guildName = guildEvent.getGuild().getName();
		}
		if (event instanceof Interaction interaction && interaction.getGuild() != null) {
			guildId = interaction.getGuild().getId();
			guildName = interaction.getGuild().getName();
		}
		EVENTS.labels(event.getClass().getSimpleName(), guildId, guildName).inc();
	}

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		MESSAGES_SENT.labels(event.getGuild().getId(), event.getGuild().getName(), event.getChannel().getId(), event.getChannel().getName()).inc();
		TOTAL_MESSAGES_SENT.inc();
	}
}
