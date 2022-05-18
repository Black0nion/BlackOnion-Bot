package com.github.black0nion.blackonionbot.mongodb;

import com.github.black0nion.blackonionbot.misc.RunMode;
import com.github.black0nion.blackonionbot.utils.config.Config;
import com.mongodb.client.MongoDatabase;

public class MongoDB {

	private static MongoDB instance;

	public static MongoDB getInstance() {
		return instance;
	}

	public MongoDB() {
		instance = this;
	}

	private final MongoDatabase database = MongoManager.getDatabase("BlackOnion-Bot" + (Config.getInstance().getRunMode() != RunMode.RELEASE ? "_" + Config.getInstance().getRunMode().name() : ""));

	public MongoDatabase getDatabase() {
		return database;
	}
}