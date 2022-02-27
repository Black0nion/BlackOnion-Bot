package com.github.black0nion.blackonionbot.systems.logging;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.influx.InfluxManager;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.utils.Utils;
import org.bson.Document;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author _SIM_
 *
 */
@SuppressWarnings("ALL")
// TODO: implement
public class StatisticsManager {

	private static long commandsLastTenSeconds;
	private static long totalCommands;
	private static long messagesSent;
	private static long messagesSentLastTenSeconds;
	private static int profanityFilteredLastTenSecs;

	private static final float mb = 1024 * 1024;

	@Reloadable("statistics")
	public static void init() {
		// TODO: load missing stats
		Bot.scheduledExecutor.scheduleAtFixedRate(StatisticsManager::saveStats, 0, 10, TimeUnit.SECONDS);
	}

	public static void saveStats() {
		InfluxManager.save("stats", new Document("cmdcount", commandsLastTenSeconds).append("messagecount", messagesSentLastTenSeconds).append("profanityfiltered", profanityFilteredLastTenSecs));
		InfluxManager.save("guilds", new Document());
		commandsLastTenSeconds = 0;
		messagesSentLastTenSeconds = 0;
		profanityFilteredLastTenSecs = 0;
	}

	public static double getProcessRamLoad() {
		final Runtime runtime = Runtime.getRuntime();
		return (runtime.totalMemory() - runtime.freeMemory()) / mb;
	}

	public static double getProcessMaxRamLoad() {
		final Runtime runtime = Runtime.getRuntime();
		return runtime.maxMemory() / mb;
	}

	public static int getGuildCount() {
		return Bot.jda.getGuilds().size();
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

	public static void messageSent() {
		messagesSentLastTenSeconds++;
	}

	public static long getMessagesSent() {
		return messagesSent;
	}

	public static void commandExecuted() {
		commandsLastTenSeconds++;
	}

	public static long getTotalCommands() {
		return totalCommands;
	}

	public static void profanityFiltered() {
		profanityFilteredLastTenSecs++;
	}

	public static void setLineCount(int newLineCount) {
		// TODO: implement
	}

	public static void setFileCount(int newFileCount) {
		// TODO: implement
	}

	public static int getLineCount() {
		// TODO: implement
		return -1;
	}

	public static int getFileCount() {
		// TODO: implement
		return -1;
	}
}