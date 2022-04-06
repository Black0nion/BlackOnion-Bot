package com.github.black0nion.blackonionbot.mongodb;

import com.github.black0nion.blackonionbot.misc.RunMode;
import com.github.black0nion.blackonionbot.utils.config.Config;
import com.mongodb.client.MongoDatabase;

public class MongoDB {
	public static final MongoDatabase DATABASE = MongoManager
			.getDatabase("BlackOnion-Bot" + (Config.run_mode != RunMode.RELEASE ? "_" + Config.run_mode.name() : ""));
	public static final MongoDatabase generalDatabase = MongoManager.getDatabase("General");
}
