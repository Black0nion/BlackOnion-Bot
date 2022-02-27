package com.github.black0nion.blackonionbot.mongodb;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.misc.RunMode;
import com.mongodb.client.MongoDatabase;

public class MongoDB {
	public static final MongoDatabase DATABASE = MongoManager.getDatabase("BlackOnion-Bot" + (Bot.runMode != RunMode.RELEASE ? "_" + Bot.runMode.name() : ""));
	public static final MongoDatabase generalDatabase = MongoManager.getDatabase("General");
}