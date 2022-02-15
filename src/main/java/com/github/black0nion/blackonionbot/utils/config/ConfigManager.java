package com.github.black0nion.blackonionbot.utils.config;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.systems.logging.Logger;
import com.github.black0nion.blackonionbot.utils.BlackIncrementor;
import com.github.black0nion.blackonionbot.utils.Utils;

import java.io.*;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ConfigManager {

	private static final File ENV_FILE = new File("files/.env");
	static BotMetadata metadata;

	private static final Pattern ENV_FILE_PATTERN = Pattern.compile("^([A-Za-z0-9_]+)=(.*)$");;
	public static void loadConfig() throws IOException {
		// Load .env vars from the .env file
		if (ENV_FILE.exists()) {
			Logger.logInfo("Loading environment variables from the .env file");
			BlackIncrementor count = new BlackIncrementor();
			Files.readAllLines(ENV_FILE.toPath())
				.stream()
				.filter(line -> !line.startsWith("#"))
				.map(ENV_FILE_PATTERN::matcher)
				.filter(Matcher::matches)
				.peek(count::increment)
				.forEach(split -> System.setProperty(split.group(1), split.group(2)));
			Logger.logInfo("Loaded " + count.getCount() + " environment variables from the .env file");
		} else {
			Logger.logWarning("No .env file found, skipping loading environment variables");
		}

		// Load Metadata
		Logger.logInfo("Loading metadata...");
		try (InputStream in = ConfigManager.class.getResourceAsStream("/bot.metadata.json")) {
			assert in != null;
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
				// Use resource
				metadata = Bot.gson.fromJson(reader.lines().collect(Collectors.joining("\n")), BotMetadata.class);
				Logger.logInfo("Loaded metadata!");
			} catch (IOException e) {
				Logger.logError("Failed to load metadata: " + e.getMessage());
			}
		}
		if (metadata == null) {
			Logger.logError("Failed to load metadata, defaulting");
			metadata = new BotMetadata();
		}
	}

	public static void saveConfig() {
		throw new RuntimeException("Not implemented");
	}
}