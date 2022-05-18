package com.github.black0nion.blackonionbot.stats;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.utils.config.BotMetadata;
import com.github.black0nion.blackonionbot.utils.config.Config;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.hotspot.DefaultExports;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class StatisticsManager extends ListenerAdapter {

	static {
		// JVM Stats, collected by Prometheus
		DefaultExports.initialize();
	}

	public static final long STARTUP_TIME = System.currentTimeMillis();
	private static final String NAMESPACE = "blackonionbot";

	static {
		final BotMetadata metadata = Config.getInstance().getMetadata();
		Gauge.build()
			.name("info")
			.help("Build information")
			.namespace(NAMESPACE)
			.labelNames("run_mode", "version", "lines_of_code", "files")
			.register()
			.labels(
				Config.getInstance().getRunMode().name(),
				metadata.version(),
				String.valueOf(metadata.lines_of_code()),
				String.valueOf(metadata.files()))
			.set(1);
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
		.labelNames("type", "command", "guild_id", "guild", "channel_id", "channel")
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
		.labelNames("guild_id", "guild", "channel_id", "channel")
		.register();

	/**
	 * Only required for internal use.
	 */
	public static final Counter TOTAL_MESSAGES_SENT = Counter.build()
		.name("total_messages_sent")
		.help("Total number of messages sent")
		.namespace(NAMESPACE)
		.create();

	public static final Counter PROFANITY_FILTERED = Counter.build()
		.name("profanity_filtered")
		.help("Total number of profanity filtered")
		.namespace(NAMESPACE)
		.labelNames("guild_id", "guild", "channel_id", "channel")
		.register();

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
		.labelNames("type", "guild_id", "guild")
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
		return guildCount = (int) Bot.getInstance().getJda().getGuildCache().size();
	}

	public static int getGuildCount() {
		return guildCount;
	}

	private static long userCount = -1;

	static long reloadUserCount() {
		return userCount = Bot.getInstance().getJda().getGuildCache().stream().map(Guild::getMemberCount).mapToInt(Integer::intValue).sum();
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
			e.printStackTrace();
		}
		return Double.NaN;
	}

	@Override
	public void onGenericEvent(@NotNull GenericEvent event) {
		String guildId = "none", guildName = "none";
		if (event instanceof GenericGuildEvent guildEvent) {
			guildId = guildEvent.getGuild().getId();
			guildName = guildEvent.getGuild().getName();
		}
		EVENTS.labels(event.getClass().getSimpleName(), guildId, guildName).inc();
	}

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		MESSAGES_SENT.labels(event.getGuild().getId(), event.getGuild().getName(), event.getChannel().getId(), event.getChannel().getName());
		TOTAL_MESSAGES_SENT.inc();
	}
}