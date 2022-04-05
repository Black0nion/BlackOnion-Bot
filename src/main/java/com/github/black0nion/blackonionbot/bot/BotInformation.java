package com.github.black0nion.blackonionbot.bot;

import com.github.black0nion.blackonionbot.misc.GuildType;
import com.github.black0nion.blackonionbot.misc.OS;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.google.common.io.Files;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import static com.github.black0nion.blackonionbot.misc.OS.*;

public class BotInformation {

  public static final SimpleDateFormat DATE_PATTERN = new SimpleDateFormat("dd.MM.yyy HH:mm");

  public static OperatingSystemMXBean OSBEAN = ManagementFactory.getOperatingSystemMXBean();
  public static OS OS;
  public static String OS_NAME = "N/A";

  public static String CPU_NAME = "N/A";
  public static String CPU_MHZ = "N/A";

  public static long SELF_USER_ID;

  public static long botLogsChannel;
  public static long supportServer;

  @Reloadable("botinformation")
  public static void init() {
    try {
      final Document doc =
          BlackGuild.configs.find(Filters.eq("guildtype", GuildType.SUPPORT_SERVER.name())).first();
      if (doc != null && doc.containsKey("guildid") && doc.containsKey("botlogschannel")) {
        supportServer = doc.getLong("guildid");
        botLogsChannel = doc.getLong("botlogschannel");
      }
    } catch (Exception ignored) {
      LoggerFactory.getLogger(BotInformation.class)
          .warn("Could not load support server information from database");
    }

    try {
      if (OS == null) {
        if (OSBEAN.getName().toLowerCase().contains("windows")) {
          OS = WINDOWS;
          OS_NAME = OSBEAN.getName();
        } else if (OSBEAN.getName().toLowerCase().contains("mac")) {
          OS = MACOS;
          OS_NAME = "macOS :vomitting:";
        } else if (OSBEAN.getName().toLowerCase().contains("linux")) {
          OS = LINUX;
          final File cpuinfofile = new File("/etc/os-release");
          final HashMap<String, String> osInfo = new HashMap<>();
          final List<String> input = Files.readLines(cpuinfofile, StandardCharsets.UTF_8);
          for (final String key : input) {
            final String[] pair = key.split("=", 2);
            osInfo.put(pair[0].trim(), pair.length == 1 ? "" : pair[1].trim());
          }

          OS_NAME = osInfo.get("PRETTY_NAME").replace("\"", "");
        } else {
          OS = UNKNOWN;
          OS_NAME = "UNKOWN";
        }
      }

      if (OS != WINDOWS) {
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

      if (!"N/A".equals(CPU_NAME))
        CPU_MHZ = CPU_MHZ.charAt(0) + "," + CPU_MHZ.substring(1, 3) + " GHz";

      if (CPU_NAME.contains("@")) {
        CPU_NAME = CPU_NAME.split("@")[0].trim();
      }

      if (!"N/A".equals(OS_NAME))
        CPU_NAME = CPU_NAME.replace("CPU", "").trim().replaceAll("\\s+", " ");
    } catch (final Exception e) {
      LoggerFactory.getLogger(BotInformation.class).error("Failed to load bot information", e);
    }
  }
}
