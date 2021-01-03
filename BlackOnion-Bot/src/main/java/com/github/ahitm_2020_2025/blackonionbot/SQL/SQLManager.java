package com.github.ahitm_2020_2025.blackonionbot.SQL;

public class SQLManager {
	public static void onCreate() {
		LiteSQL.onUpdate("CREATE TABLE IF NOT EXISTS reactroles(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, guildid INTEGER, channelid INTEGER, messageid INTEGER, emote VARCHAR, roleid INTEGER)");
	}
}
