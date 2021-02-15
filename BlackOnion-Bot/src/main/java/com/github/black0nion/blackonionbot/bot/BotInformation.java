package com.github.black0nion.blackonionbot.bot;

import static com.sun.jna.platform.win32.WinReg.HKEY_LOCAL_MACHINE;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import com.github.black0nion.blackonionbot.Logger;
import com.github.black0nion.blackonionbot.enums.LogOrigin;
import com.github.black0nion.blackonionbot.enums.OS;
import com.github.black0nion.blackonionbot.systems.guildmanager.GuildManager;
import com.github.black0nion.blackonionbot.utils.ValueManager;
import com.google.common.io.Files;
import com.sun.jna.platform.win32.Advapi32Util;

import net.dv8tion.jda.api.entities.Guild;

public class BotInformation {
	public static int line_count = 0;
	public static int file_count = 0;

	public static OperatingSystemMXBean osBean;
	public static OS os;
	public static String osName;
	
	public static String cpuName = "N/A";
	public static String cpuMhz = "N/A";
	
	public static String defaultPrefix;
	private static HashMap<String, String> guildPrefixes = new HashMap<>();

	public static Color mainColor = Color.getHSBColor(0.8F, 1, 0.5F);
	
	public static long botId;

	public static void init() {
		
		guildPrefixes.clear();
		for (org.bson.Document doc : GuildManager.getAllConfigs()) if (doc.containsKey("prefix")) guildPrefixes.put(doc.getString("guildid"), doc.getString("prefix"));
		
		try {
			calculateCodeLines();
			if (osBean == null)
				osBean = ManagementFactory.getOperatingSystemMXBean();

			if (os == null) {
				if (osBean.getName().toLowerCase().contains("windows")) {
					os = OS.WINDOWS;
					osName = osBean.getName();
				} else {
					os = OS.LINUX;
					File cpuinfofile = new File("/etc/os-release");
					HashMap<String, String> osInfo = new HashMap<>();
					List<String> input = Files.readLines(cpuinfofile, StandardCharsets.UTF_8);
					for (String key : input) {
						String[] pair = key.split("=", 2);
						osInfo.put(pair[0].trim(), pair.length == 1 ? "" : pair[1].trim());
					}

					osName = osInfo.get("PRETTY_NAME").replace("\"", "");
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
			File dir = new File("src/main/java/com/github/black0nion/blackonionbot");
			File[] files = dir.listFiles();
			line_count = 1337;
			file_count = 69;
			new Thread(new Runnable() {
				@Override
				public void run() {
					showFiles(files);
					ValueManager.save("lines", line_count);
					ValueManager.save("files", file_count);
					//Send a request to the servers to update the two counters
					try {
						for (String endpoint : Files.readLines(new File("files/endpoints.txt"), StandardCharsets.UTF_8)) {
							new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										String body = "{\"line_count\":" + line_count + ",\"file_count\":" + file_count + "}";
										URL url = new URL(endpoint + "/api/updatefilelinecount");
										HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
										httpCon.setDoOutput(true);
										httpCon.setRequestMethod("POST");
										httpCon.setRequestProperty("token", "updatepls");
										OutputStream os = httpCon.getOutputStream();
										OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");    
										osw.write(body);
										osw.flush();
										osw.close();
										os.close();
										httpCon.connect();
										httpCon.getInputStream();
									} catch (Exception e) {
										if (!(e instanceof ConnectException))
											e.printStackTrace();
									}
								}
							}).start();
						}
					} catch (Exception e) {
						Logger.logWarning("No file \"files/endpoints.txt\"", LogOrigin.API);
					}
				}
			}).start();
		}
	}
	
	public static void showFiles(File[] files) {
		line_count = 0;
		file_count = 0;
		
		searchDirectory(files);
	}

	public static void searchDirectory(File[] files) {
		try {
			for (File file : files) {
				if (file.isDirectory()) {
					searchDirectory(file.listFiles());
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
	
	public static String getPrefix(Guild guild) {
		return getPrefix(guild.getId());
	}
	
	public static String getPrefix(String guildID) {
		if (guildPrefixes.containsKey(guildID))
			return guildPrefixes.get(guildID);
		return defaultPrefix;
	}
	
	public static void setPrefix(Guild guild, String prefix) {
		setPrefix(guild.getId(), prefix);
	}

	public static void setPrefix(String guildId, String prefix) {
		guildPrefixes.put(guildId, prefix);
		GuildManager.saveString(guildId, "prefix", prefix);
	}
}
