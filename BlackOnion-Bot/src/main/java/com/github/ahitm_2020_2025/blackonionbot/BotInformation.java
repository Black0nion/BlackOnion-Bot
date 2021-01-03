package com.github.ahitm_2020_2025.blackonionbot;

import static com.sun.jna.platform.win32.WinReg.HKEY_LOCAL_MACHINE;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import com.google.common.io.Files;
import com.sun.jna.platform.win32.Advapi32Util;

import com.github.ahitm_2020_2025.blackonionbot.bot.Bot;
import com.github.ahitm_2020_2025.blackonionbot.enums.OS;
import com.github.ahitm_2020_2025.blackonionbot.utils.ValueManager;

public class BotInformation {
	public static int line_count = 0;
	public static int file_count = 0;

	public static OperatingSystemMXBean osBean;
	public static OS os;
	
	public static String cpuName = "N/A";
	public static String cpuMhz = "N/A";

	public static void init() {
		try {
			calculateCodeLines();
			if (osBean == null)
				osBean = ManagementFactory.getOperatingSystemMXBean();

			if (os == null) {
				if (osBean.getName().toLowerCase().contains("windows")) {
					os = OS.WINDOWS;
				} else {
					os = OS.LINUX;
				}
			}
			
			if (os == OS.WINDOWS) {
				cpuName = Advapi32Util.registryGetStringValue(HKEY_LOCAL_MACHINE,
						"HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0\\", "ProcessorNameString");
				cpuMhz = String.valueOf(Advapi32Util.registryGetValue(HKEY_LOCAL_MACHINE,
						"HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0\\", "~MHZ"));
			} else {
				File cpuinfofile = new File("/proc/cpuinfo");
				HashMap<String, String> cpuinfo = new HashMap<>();
				List<String> input = Files.readLines(cpuinfofile, StandardCharsets.UTF_8);
				for (String key : input) {
					String[] pair = key.split(":", 2);
					cpuinfo.put(pair[0].trim(), pair.length == 1 ? "" : pair[1].trim());
				}

				cpuName = cpuinfo.get("model name");
				cpuMhz = cpuinfo.get("cpu MHz");
			}

			cpuMhz = cpuMhz.substring(0, 1) + "," + cpuMhz.substring(1, 3) + " GHz";

			if (cpuName.contains("@")) {
				cpuName = cpuName.split("@")[0].trim();
			}

			cpuName = cpuName.replace("CPU", "").trim().replaceAll(" s", " ");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void calculateCodeLines() {
		if (Bot.isJarFile) {
			line_count = ValueManager.getInt("lines");
			file_count = ValueManager.getInt("files");
		} else {
			File dir = new File("src/main/java/com/github/ahitm_2020_2025/blackonionbot");
			File[] files = dir.listFiles();
			showFiles(files);
			ValueManager.save("lines", line_count);
			ValueManager.save("files", file_count);
		}
	}

	public static void showFiles(File[] files) {
		try {
			for (File file : files) {
				if (file.isDirectory()) {
					showFiles(file.listFiles());
				} else {
					file_count++;
					BufferedReader reader = new BufferedReader(new FileReader(file));
					while (reader.readLine() != null)
						line_count++;
					reader.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String prefix;
}
