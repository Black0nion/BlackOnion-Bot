package com.github.black0nion.blackonionbot.systems.guildmanager;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.mongodb.MongoManager;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;

public class GuildManager {
	
	private static MongoCollection<Document> collection;
	
	public static void init() {
		if (collection == null)
			collection = MongoManager.getCollection("guildsettings", MongoDB.botDatabase);
	}

	public static List<Document> getAllConfigs() {
		return collection.find().into(new ArrayList<>());
	}
	
	public static String getString(String guild, String key) {
		return MongoManager.getDocumentInCollection(collection, "guildid", guild).getString(key);
	}
	
	public static String getString(String guild, String key, String defaultValue) {
		final Document doc = MongoManager.getDocumentInCollection(collection, "guildid", guild);
		if (doc.containsKey(key))
			return doc.getString(key);
		else {
			MongoManager.updateValue(collection, new BasicDBObject().append("guildid", guild), new Document(key, defaultValue));
			return defaultValue;
		}
	}
	
	public static void saveString(String guild, String key, Object value) {
		MongoManager.updateValue(collection, new BasicDBObject().append("guildid", guild), new Document().append("guildid", guild).append(key, value));
	}
}
