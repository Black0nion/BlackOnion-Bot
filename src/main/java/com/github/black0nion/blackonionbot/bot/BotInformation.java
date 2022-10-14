package com.github.black0nion.blackonionbot.bot;

import com.github.black0nion.blackonionbot.misc.OperatingSystem;
import com.google.common.io.Files;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

import static com.github.black0nion.blackonionbot.misc.OperatingSystem.*;

public class BotInformation {
	private BotInformation() {}

	private static final String PATTERN = "dd.MM.yyyy HH:mm";

	@SuppressWarnings("java:S2885")
	public static final SimpleDateFormat datePattern = new SimpleDateFormat(PATTERN);

	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

	public static final OperatingSystemMXBean OS_BEAN = ManagementFactory.getOperatingSystemMXBean();
	public static final OperatingSystem OPERATING_SYSTEM;
	public static final String OS_NAME;

	public static final String CPU_NAME;
	public static final String CPU_MHZ;

	static {
		String osName = "Unknown";
		String cpuName = "N/A";
		String cpuMhz = "N/A";
		OperatingSystem operatingSystem = UNKNOWN;
		try {
			if (OS_BEAN.getName().toLowerCase().contains("windows")) {
				operatingSystem = WINDOWS;
				osName = OS_BEAN.getName();
			} else if (OS_BEAN.getName().toLowerCase().contains("mac")) {
				operatingSystem = MACOS;
				osName = "macOS :vomitting:";
			} else if (OS_BEAN.getName().toLowerCase().contains("linux")) {
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
