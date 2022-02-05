package com.github.black0nion.blackonionbot.utils.config;

import com.github.black0nion.blackonionbot.systems.logging.Logger;
import com.github.black0nion.blackonionbot.utils.BlackIncrementor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ConfigManager {

	private static final File ENV_FILE = new File("files/.env");

	public static void loadConfig() throws IOException {
		// Load .env vars from the .env file
		if (ENV_FILE.exists()) {
			Logger.logInfo("Loading environment variables from the .env file");
			BlackIncrementor count = new BlackIncrementor();
			Files.readAllLines(ENV_FILE.toPath())
				.stream()
				.filter(line -> !line.startsWith("#"))
				.map(line -> line.replaceAll("\\s+=\\s+", "="))
				.map(line -> line.split("="))
				.peek(count::increment)
				.forEach(split -> System.setProperty(split[0], split[1]));
			Logger.logInfo("Loaded " + count.getCount() + " environment variables from the .env file");
		} else {
			Logger.logWarning("No .env file found, skipping loading environment variables");
		}
	}

	public static void saveConfig() {
		throw new RuntimeException("Not implemented");
	}
}