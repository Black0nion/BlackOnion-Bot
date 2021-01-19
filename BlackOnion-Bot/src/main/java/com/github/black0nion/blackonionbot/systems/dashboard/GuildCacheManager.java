package com.github.black0nion.blackonionbot.systems.dashboard;

import java.util.ArrayList;

public class GuildCacheManager {
	
	private static ArrayList<CachedGuildInfo> cachedGuildInfo = new ArrayList<>();
	
	public static CachedGuildInfo getCache(String guildId) {
		for (int i = 0; i < cachedGuildInfo.size(); i++) {
			CachedGuildInfo guildInfo = cachedGuildInfo.get(i);
			if (guildInfo.getGuild().getId().equals(guildId)) {
				return guildInfo;
			}
		}
		return null;
	}
}
