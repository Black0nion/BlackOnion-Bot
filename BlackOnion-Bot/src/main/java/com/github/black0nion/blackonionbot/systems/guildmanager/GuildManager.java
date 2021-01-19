package com.github.black0nion.blackonionbot.systems.guildmanager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Guild;

public class GuildManager {
	
	private static List<GuildSettings> guildOptions = new ArrayList<>();
	
	public static void init() {
		guildOptions.clear();
		for (File f : new File("files/guildoptions").listFiles()) {
			guildOptions.add(new GuildSettings(f.getName().replace(".json", "")));
		}
	}
	
	public static GuildSettings createGuildOptions(String guildId) {
		if (!guildOptions.stream().anyMatch(guildOption -> guildOption.guildId.equals(guildId))) {
			GuildSettings newSettings = new GuildSettings(guildId);
			guildOptions.add(newSettings);
			return newSettings;
		}
		return getGuildSettings(guildId);
	}
	
	public static GuildSettings getGuildSettings(Guild guild) {
		return getGuildSettings(guild.getId());
	}
	
	public static GuildSettings getGuildSettings(String guildId) {
		return guildOptions.stream().anyMatch(guildOption -> guildOption.guildId.equals(guildId)) ? guildOptions.stream().filter(guildOption -> guildOption.guildId.equals(guildId)).findFirst().get() : createGuildOptions(guildId);
	}
	
	public static List<GuildSettings> getAllGuildOptions() {
		return guildOptions;
	}
}
