package com.github.black0nion.blackonionbot.mongodb;

import com.github.black0nion.blackonionbot.misc.RunMode;
import com.github.black0nion.blackonionbot.config.api.Config;
import com.mongodb.client.MongoDatabase;

import javax.annotation.Nullable;

public class MongoDB {
	private static MongoDB instance;

	@Nullable
	public static MongoDB getInstance() {
		return instance;
	}

	public MongoDB(Config config) {
		instance = this;
		database = MongoManager.getDatabase("BlackOnion-Bot" + (config.getRunMode() != RunMode.RELEASE ? "_" + config.getRunMode().name() : ""));
	}

	private final MongoDatabase database;

	public MongoDatabase getDatabase() {
		return database;
	}
}
