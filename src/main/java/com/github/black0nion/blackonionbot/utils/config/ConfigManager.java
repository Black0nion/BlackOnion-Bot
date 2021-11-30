package com.github.black0nion.blackonionbot.utils.config;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.systems.logging.Logger;
import com.github.black0nion.blackonionbot.utils.Async;
import com.google.gson.Gson;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;

public class ConfigManager {

	private static final File CONFIG_FILE = new File("files", "config.json");
	private static final Gson GSON = new Gson().newBuilder()
			.setFieldNamingStrategy(f -> f.getName().toLowerCase())
			.serializeNulls()
			.registerTypeAdapterFactory(new NonNullTypeAdapterFactory())
			.create();

	public static void loadConfig() throws IOException {
		if (!CONFIG_FILE.exists() || Bot.launchArguments.contains("--reset-config") || Bot.launchArguments.contains("--generate-config") || Files.readString(CONFIG_FILE.toPath()).isEmpty()) {
			CONFIG_FILE.getParentFile().mkdirs();
			CONFIG_FILE.createNewFile();
			saveConfig();
			Logger.logWarning("Config file created - please modify it!");
			System.exit(1);
		}
		try {
			Config newConfig = GSON.fromJson(System.getenv("CONFIG") != null ? System.getenv("CONFIG") : Files.readString(CONFIG_FILE.toPath()), Config.class);
			Config.setConfig(newConfig);
		} catch (Exception e) {
			Logger.logError("Failed to load config file: " + e.getMessage());
			System.exit(1);
		}
	}

	@Async
	public static void saveConfig() {
		saveConfig(null);
    }

	@Async
	public static void saveConfig(Consumer<JSONObject> onFinish) {
		try {
			JSONObject cnfg = new JSONObject(GSON.toJson(Config.getConfig()));
			System.out.println(cnfg);
			Files.writeString(CONFIG_FILE.toPath(), cnfg.toString(2));
			if (onFinish != null) {
				onFinish.accept(cnfg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}