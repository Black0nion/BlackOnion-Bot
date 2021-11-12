package com.github.black0nion.blackonionbot.bot;

import static com.sun.jna.platform.win32.WinReg.HKEY_LOCAL_MACHINE;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import org.bson.Document;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.misc.GuildType;
import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.misc.OS;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.systems.logging.Logger;
import com.github.black0nion.blackonionbot.utils.ValueManager;
import com.google.common.io.Files;
import com.mongodb.client.model.Filters;
import com.sun.jna.platform.win32.Advapi32Util;

@SuppressWarnings("UnstableApiUsage")
public class BotInformation {

	public static final SimpleDateFormat DATE_PATTERN = new SimpleDateFormat("dd.MM.yyy HH:mm");

	public static int LINE_COUNT = -1;
	public static int FILE_COUNT = -1;

	public static OperatingSystemMXBean OSBEAN;
	public static OS OS;
	public static String OS_NAME;

	public static String CPU_NAME = "N/A";
	public static String CPU_MHZ = "N/A";

	public static String DEFAULT_PREFIX = "*";

	public static long SELF_USER_ID;

	public static long botLogsChannel;
	public static long supportServer;

	public static String BOT_VERSION = "N/A";

	@Reloadable("botinformation")
	public static void init() {
		Bot.executor.submit(BotInformation::calculateCodeLines);
		Bot.executor.submit(() -> {
			try {
				final File file = new File("version");
				if (file.exists()) {
					final @Nullable String readFirstLine = Files.asCharSource(file, StandardCharsets.UTF_8).readFirstLine();
					if (readFirstLine != null) {
						BOT_VERSION = readFirstLine;
					}
				}
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
			final Document doc = BlackGuild.configs.find(Filters.eq("guildtype", GuildType.SUPPORT_SERVER.name())).first();
			if (doc != null) {
				supportServer = doc.getLong("guildid");
				botLogsChannel = doc.getLong("botlogschannel");
			}

			try {
				System.out.println(OSBEAN);
				if (OSBEAN == null) {
					OSBEAN = ManagementFactory.getOperatingSystemMXBean();
				}

				if (OS == null) {
					if (OSBEAN.getName().toLowerCase().contains("windows")) {
						OS = com.github.black0nion.blackonionbot.misc.OS.WINDOWS;
						OS_NAME = OSBEAN.getName();
					} else if (OSBEAN.getName().toLowerCase().contains("mac")) {
						OS = com.github.black0nion.blackonionbot.misc.OS.MACOS;
						OS_NAME = "macOS :vomitting:";
					} else if (OSBEAN.getName().toLowerCase().contains("linux")) {
						OS = com.github.black0nion.blackonionbot.misc.OS.LINUX;
						final File cpuinfofile = new File("/etc/os-release");
						final HashMap<String, String> osInfo = new HashMap<>();
						final List<String> input = Files.readLines(cpuinfofile, StandardCharsets.UTF_8);
						for (final String key : input) {
							final String[] pair = key.split("=", 2);
							osInfo.put(pair[0].trim(), pair.length == 1 ? "" : pair[1].trim());
						}

						OS_NAME = osInfo.get("PRETTY_NAME").replace("\"", "");
					} else {
						OS = com.github.black0nion.blackonionbot.misc.OS.UNKNOWN;
						OS_NAME = "UNKOWN";
					}
				}

				if (OS == com.github.black0nion.blackonionbot.misc.OS.WINDOWS) {
					CPU_NAME = Advapi32Util.registryGetStringValue(HKEY_LOCAL_MACHINE, "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0\\", "ProcessorNameString");
					CPU_MHZ = String.valueOf(Advapi32Util.registryGetValue(HKEY_LOCAL_MACHINE, "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0\\", "~MHZ"));
				} else {
					final File cpuinfofile = new File("/proc/cpuinfo");
					final HashMap<String, String> cpuinfo = new HashMap<>();
					final List<String> input = Files.readLines(cpuinfofile, StandardCharsets.UTF_8);
					for (final String key : input) {
						final String[] pair = key.split(":", 2);
						cpuinfo.put(pair[0].trim(), pair.length == 1 ? "" : pair[1].trim());
					}

					CPU_NAME = cpuinfo.get("model name");
					CPU_MHZ = cpuinfo.get("cpu MHz");
				}

				CPU_MHZ = CPU_MHZ.charAt(0) + "," + CPU_MHZ.substring(1, 3) + " GHz";

				if (CPU_NAME.contains("@")) {
					CPU_NAME = CPU_NAME.split("@")[0].trim();
				}

				CPU_NAME = CPU_NAME.replace("CPU", "").trim().replaceAll(" s", " ");
			} catch (final Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static void calculateCodeLines() {
		if (Bot.isJarFile) {
			LINE_COUNT = ValueManager.getInt("lines");
			FILE_COUNT = ValueManager.getInt("files");
		} else {
			final File dir = new File("src/main");
			final File[] files = dir.listFiles();
			LINE_COUNT = 1337;
			FILE_COUNT = 69;
			Bot.executor.submit(() -> {
				showFiles(files);
				ValueManager.save("lines", LINE_COUNT);
				ValueManager.save("files", FILE_COUNT);
				// Send a request to the servers to update the two counters
				try {
					for (final String endpoint : Files.readLines(new File("files/endpoints.txt"), StandardCharsets.UTF_8)) {
						Bot.executor.submit(() -> {
							try {
								final String body = "{\"line_count\":" + LINE_COUNT + ",\"file_count\":" + FILE_COUNT + "}";
								final URL url = new URL(endpoint + "/api/updatefilelinecount");
								final HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
								httpCon.setDoOutput(true);
								httpCon.setRequestMethod("POST");
								httpCon.setRequestProperty("token", "updatepls");
								final OutputStream os = httpCon.getOutputStream();
								final OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
								osw.write(body);
								osw.flush();
								osw.close();
								os.close();
								httpCon.connect();
								httpCon.getInputStream();
							} catch (final Exception e) {
								if (!(e instanceof ConnectException)) {
									e.printStackTrace();
								}
							}
						});
					}
				} catch (final Exception e) {
					Logger.logWarning("No file \"files/endpoints.txt\"", LogOrigin.API);
				}
			});
		}
	}

	public static void showFiles(final File[] files) {
		LINE_COUNT = 0;
		FILE_COUNT = 0;

		searchDirectory(files);
	}

	public static void searchDirectory(final File[] files) {
		try {
			for (final File file : files) {
				if (file.isDirectory()) {
					searchDirectory(file.listFiles());
				} else {
					FILE_COUNT++;
					final String fileName = file.getName();
					if (fileName.endsWith(".png") || fileName.endsWith(".jpg")) {
						continue;
					}
					final BufferedReader reader = new BufferedReader(new FileReader(file));
					int count = 0;
					while (reader.readLine() != null) {
						count++;
					}
					LINE_COUNT += count;
					reader.close();
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}