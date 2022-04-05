package com.github.black0nion.blackonionbot.utils.config;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.utils.Incrementer;
import com.github.black0nion.blackonionbot.utils.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ConfigManager {

  private static final File ENV_FILE = new File("files/.env");
  static BotMetadata metadata;

  private static final Pattern ENV_FILE_PATTERN = Pattern.compile("^(\\w+)=(.*)$");

  private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);

  public static void loadConfig() throws IOException {
    // Load .env vars from the .env file
    if (ENV_FILE.exists()) {
      logger.info("Loading environment variables from the .env file...");
      Incrementer count = new Incrementer();
      Files.readAllLines(ENV_FILE.toPath()).stream().filter(line -> !line.startsWith("#"))
          .map(ENV_FILE_PATTERN::matcher).filter(Matcher::matches).peek(count::increment)
          .forEach(split -> set(split.group(1), split.group(2)));
      logger.info("Loaded " + count.getCount() + " environment variables from the .env file");
    } else {
      logger.info("No .env file found, skipping loading environment variables");
    }

    // Load Metadata
    logger.info("Loading metadata...");
    try (InputStream in = ConfigManager.class.getResourceAsStream("/bot.metadata.json")) {
      assert in != null;
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
        // Use resource
        metadata = Bot.getInstance().getGson()
            .fromJson(reader.lines().collect(Collectors.joining("\n")), BotMetadata.class);
        logger.info("Loaded metadata!");
      } catch (Exception e) {
        logger.error("Failed to load metadata", e);
      }
    }
    if (metadata == null) {
      logger.error("Failed to load metadata, defaulting");
      metadata = new BotMetadata();
    }
  }

  public static void set(String key, String value) {
    System.setProperty(key.toUpperCase(Locale.ROOT), value);
  }

  public static void saveConfig() {
    throw new NotImplementedException("Save Config");
  }
}
