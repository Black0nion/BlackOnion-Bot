package com.github.black0nion.blackonionbot.systems.guildmanager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GuildManager {
	
	private static List<GuildSettings> guildOptions = new ArrayList<>();
	
	public static void init() {
		for (File f : new File("files/guildsettings").listFiles()) {
			guildOptions.add(new GuildSettings(f.getName().replace(".json", "")));
		}
	}
	
	public static void createGuildOptions(String guildId) {
		if (getGuildSettings(guildId) == null)
			guildOptions.add(new GuildSettings(guildId));
	}
	
	public static GuildSettings getGuildSettings(String guildId) {
		return guildOptions.stream().filter(guildOption -> guildOption.guildId.equals(guildId)).findFirst().orElse(null);
	}
}
