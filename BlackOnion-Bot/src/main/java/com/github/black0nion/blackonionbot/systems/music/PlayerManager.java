package com.github.black0nion.blackonionbot.systems.music;

import java.util.concurrent.ConcurrentHashMap;

import com.github.black0nion.blackonionbot.bot.Bot;

public class PlayerManager {
	public ConcurrentHashMap<Long, MusicController> controllers;
	
	public PlayerManager() {
		this.controllers = new ConcurrentHashMap<>();
	}
	
	public MusicController getController(long guildId) {
		if (this.controllers.containsKey(guildId))
			return this.controllers.get(guildId);
		else {
			MusicController temp = new MusicController(Bot.jda.getGuildById(guildId));
			this.controllers.put(guildId, temp);
			return temp;
		}
	}
	
	public long getGuildByPlayerHash(int hash) {
		try { return controllers.values().stream().filter(controller -> {return controller.getPlayer().hashCode() == hash;}).findFirst().get().getGuild().getIdLong(); } catch (Exception ignored) {}
		return -1;
	}
}
