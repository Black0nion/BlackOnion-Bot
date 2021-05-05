package com.github.black0nion.blackonionbot.mongodb;

import com.mongodb.client.MongoDatabase;

public class MongoDB {
	public static final MongoDatabase botDatabase = MongoManager.getDatabase("BlackOnion-Bot");
	public static final MongoDatabase newsDatabase = MongoManager.getDatabase("General");
}
