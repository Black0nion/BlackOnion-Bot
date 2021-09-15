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

public class BotInformation {

    public static final SimpleDateFormat datePattern = new SimpleDateFormat("dd.MM.yyy HH:mm");

    public static int line_count = -1;
    public static int file_count = -1;

    public static OperatingSystemMXBean osBean;
    public static OS os;
    public static String osName;

    public static String cpuName = "N/A";
    public static String cpuMhz = "N/A";

    public static String defaultPrefix;

    public static long botId;

    public static long botLogsChannel;
    public static long supportServer;

    public static String version = "N/A";

    @Reloadable("botinformation")
    public static void init() {
	Bot.executor.submit(() -> {
	    calculateCodeLines();
	});
	Bot.executor.submit(() -> {
	    try {
		final File file = new File("version");
		if (file.exists()) {
		    final @Nullable String readFirstLine = Files.asCharSource(file, StandardCharsets.UTF_8).readFirstLine();
		    if (readFirstLine != null) {
			version = readFirstLine;
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

		if (osBean == null) {
		    osBean = ManagementFactory.getOperatingSystemMXBean();
		}

		if (os == null) {
		    if (osBean.getName().toLowerCase().contains("windows")) {
			os = OS.WINDOWS;
			osName = osBean.getName();
		    } else {
			os = OS.LINUX;
			final File cpuinfofile = new File("/etc/os-release");
			final HashMap<String, String> osInfo = new HashMap<>();
			final List<String> input = Files.readLines(cpuinfofile, StandardCharsets.UTF_8);
			for (final String key : input) {
			    final String[] pair = key.split("=", 2);
			    osInfo.put(pair[0].trim(), pair.length == 1 ? "" : pair[1].trim());
			}

			osName = osInfo.get("PRETTY_NAME").replace("\"", "");
		    }
		}

		if (os == OS.WINDOWS) {
		    cpuName = Advapi32Util.registryGetStringValue(HKEY_LOCAL_MACHINE, "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0\\", "ProcessorNameString");
		    cpuMhz = String.valueOf(Advapi32Util.registryGetValue(HKEY_LOCAL_MACHINE, "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0\\", "~MHZ"));
		} else {
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

		cpuMhz = cpuMhz.substring(0, 1) + "," + cpuMhz.substring(1, 3) + " GHz";

		if (cpuName.contains("@")) {
		    cpuName = cpuName.split("@")[0].trim();
		}

		cpuName = cpuName.replace("CPU", "").trim().replaceAll(" s", " ");
	    } catch (final Exception e) {
		e.printStackTrace();
	    }
	});
    }

    public static void calculateCodeLines() {
	if (Bot.isJarFile) {
	    line_count = ValueManager.getInt("lines");
	    file_count = ValueManager.getInt("files");
	} else {
	    final File dir = new File("src/main");
	    final File[] files = dir.listFiles();
	    line_count = 1337;
	    file_count = 69;
	    Bot.executor.submit(() -> {
		showFiles(files);
		ValueManager.save("lines", line_count);
		ValueManager.save("files", file_count);
		// Send a request to the servers to update the two counters
		try {
		    for (final String endpoint : Files.readLines(new File("files/endpoints.txt"), StandardCharsets.UTF_8)) {
			Bot.executor.submit(() -> {
			    try {
				final String body = "{\"line_count\":" + line_count + ",\"file_count\":" + file_count + "}";
				final URL url = new URL(endpoint + "/api/updatefilelinecount");
				final HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
				httpCon.setDoOutput(true);
				httpCon.setRequestMethod("POST");
				httpCon.setRequestProperty("token", "updatepls");
				final OutputStream os = httpCon.getOutputStream();
				final OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
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
	line_count = 0;
	file_count = 0;

	searchDirectory(files);
    }

    public static void searchDirectory(final File[] files) {
	try {
	    for (final File file : files) {
		if (file.isDirectory()) {
		    searchDirectory(file.listFiles());
		} else {
		    file_count++;
		    final String fileName = file.getName();
		    if (fileName.endsWith(".png") || fileName.endsWith(".jpg")) {
			continue;
		    }
		    final BufferedReader reader = new BufferedReader(new FileReader(file));
		    int count = 0;
		    while (reader.readLine() != null) {
			count++;
		    }
		    line_count += count;
		    reader.close();
		}
	    }
	} catch (final Exception e) {
	    e.printStackTrace();
	}
    }
}