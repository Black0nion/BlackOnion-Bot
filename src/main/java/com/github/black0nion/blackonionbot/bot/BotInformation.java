package com.github.black0nion.blackonionbot.bot;

import com.github.black0nion.blackonionbot.misc.GuildType;
import com.github.black0nion.blackonionbot.misc.OperatingSystem;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.google.common.io.Files;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

import static com.github.black0nion.blackonionbot.misc.OperatingSystem.*;

public class BotInformation {
	private BotInformation() {}

	public static final DateFormat datePattern = DateFormat.getDateInstance(DateFormat.SHORT);

	public static final DateFormat dateTimeFormatter = DateFormat.getDateInstance(DateFormat.SHORT);

	public static final OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
	public static final OperatingSystem OPERATING_SYSTEM;
	public static final String OS_NAME;

	public static final String CPU_NAME;
	public static final String CPU_MHZ;

	public static long logsChannel;
	public static final long SUPPORT_SERVER;

	static {
		long supportServer = -1, logsChannel = -1;
		try {
			final Document doc = BlackGuild.configs.find(Filters.eq("guildtype", GuildType.SUPPORT_SERVER.name())).first();
			if (doc != null && doc.containsKey("guildid") && doc.containsKey("botlogschannel")) {
				supportServer = doc.getLong("guildid");
				logsChannel = doc.getLong("botlogschannel");
			}
		} catch (Exception ignored) {
			LoggerFactory.getLogger(BotInformation.class).warn("Could not load support server information from database");
		}
		SUPPORT_SERVER = supportServer;
		BotInformation.logsChannel = logsChannel;

		String osName = "Unknown", cpuName = "N/A", cpuMhz = "N/A";
		OperatingSystem operatingSystem = UNKNOWN;
		try {
			if (osBean.getName().toLowerCase().contains("windows")) {
				operatingSystem = WINDOWS;
				osName = osBean.getName();
			} else if (osBean.getName().toLowerCase().contains("mac")) {
				operatingSystem = MACOS;
				osName = "macOS :vomitting:";
			} else if (osBean.getName().toLowerCase().contains("linux")) {
				operatingSystem = LINUX;
				final File cpuinfofile = new File("/etc/os-release");
				final HashMap<String, String> osInfo = new HashMap<>();
				final List<String> input = Files.readLines(cpuinfofile, StandardCharsets.UTF_8);
				for (final String key : input) {
					final String[] pair = key.split("\\s*=\\s*", 2);
					osInfo.put(pair[0].trim(), pair.length == 1 ? "" : pair[1].trim());
				}

				osName = osInfo.get("PRETTY_NAME").replace("\"", "");
			}

			if (operatingSystem != WINDOWS) {
				final File cpuinfofile = new File("/proc/cpuinfo");
				final HashMap<String, String> cpuinfo = new HashMap<>();
				final List<String> input = Files.readLines(cpuinfofile, StandardCharsets.UTF_8);
				for (final String key : input) {
					final String[] pair = key.split(":", 2);
					cpuinfo.put(pair[0].trim(), pair.length == 1 ? "" : pair[1].trim());
				}

				cpuName = cpuinfo.get("model name");
				cpuMhz = cpuinfo.get("cpu MHz");
			}

			if (!"N/A".equals(cpuMhz))
				cpuMhz = cpuMhz.charAt(0) + "," + cpuMhz.substring(1, 3) + " GHz";

			if (!"N/A".equals(cpuName)) {
				if (cpuName.contains("@"))
					cpuName = cpuName.split("@")[0].trim();

				cpuName = cpuName
					.replace("CPU", "")
					.replaceAll("\\s+", " ")
					.trim();
			}
		} catch (final Exception e) {
			LoggerFactory.getLogger(BotInformation.class).error("Failed to load bot information", e);
		}
		OS_NAME = osName;
		CPU_NAME = cpuName;
		CPU_MHZ = cpuMhz;
		OPERATING_SYSTEM = operatingSystem;
	}
}