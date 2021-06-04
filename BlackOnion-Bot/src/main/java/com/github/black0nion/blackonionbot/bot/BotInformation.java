package com.github.black0nion.blackonionbot.bot;

import static com.sun.jna.platform.win32.WinReg.HKEY_LOCAL_MACHINE;

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
import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.misc.OS;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.utils.ValueManager;
import com.google.common.io.Files;
import com.sun.jna.platform.win32.Advapi32Util;

public class BotInformation {
	public static int line_count = 0;
	public static int file_count = 0;

	public static OperatingSystemMXBean osBean;
	public static OS os;
	public static String osName;
	
	public static String cpuName = "N/A";
	public static String cpuMhz = "N/A";
	
	public static String defaultPrefix;
	
	public static long botId;

	@Reloadable("botinformation")
	public static void init() {
		try {
			Bot.executor.submit(() -> {				
				calculateCodeLines();
			});
			
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
			File dir = new File("src/main");
			File[] files = dir.listFiles();
			line_count = 1337;
			file_count = 69;
			Bot.executor.submit(() -> {
				showFiles(files);
				ValueManager.save("lines", line_count);
				ValueManager.save("files", file_count);
				//Send a request to the servers to update the two counters
				try {
					for (String endpoint : Files.readLines(new File("files/endpoints.txt"), StandardCharsets.UTF_8)) {
						Bot.executor.submit(() -> {
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
						});
					}
				} catch (Exception e) {
					Logger.logWarning("No file \"files/endpoints.txt\"", LogOrigin.API);
				}
			});
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
					final String fileName = file.getName();
					if (fileName.endsWith(".png") || fileName.endsWith(".jpg")) continue;
					BufferedReader reader = new BufferedReader(new FileReader(file));
					int count = 0;
					while (reader.readLine() != null)
						count++;
					line_count += count;
					reader.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}