package com.github.black0nion.blackonionbot.systems.guildmanager;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bson.Document;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.mongodb.MongoManager;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import net.dv8tion.jda.api.entities.Guild;

public class GuildManager {
	
	public static MongoCollection<Document> collection;
	
	public static void init() {
		if (collection == null)
			collection = MongoManager.getCollection("guildsettings", MongoDB.botDatabase);
	}

	@Nonnull
	/**
	 * @return an arraylist containing all configs, empty if none present
	 */
	public static List<Document> getAllConfigs() {
		return collection.find().into(new ArrayList<>());
	}
	
	@Nullable
	public static String getString(Guild guild, String key) {
		return getString(guild.getId(), key);
	}
	
	@Nullable
	public static String getString(String guild, String key) {
		final Document doc = MongoManager.getDocumentInCollection(collection, "guildid", guild);
		if (doc == null) return null;
		return doc.getString(key);
	}
	
	@Nonnull
	public static String getString(Guild guild, String key, String defaultValue) {
		return getString(guild.getId(), key, defaultValue);
	}
	
	@Nonnull
	public static String getString(String guild, String key, String defaultValue) {
		final Document doc = MongoManager.getDocumentInCollection(collection, "guildid", guild);
		if (doc != null && doc.containsKey(key))
			return doc.getString(key);
		else {
			MongoManager.updateValue(collection, new BasicDBObject().append("guildid", guild), new Document(key, defaultValue));
			return defaultValue;
		}
	}
	
	@Nullable
	public static boolean getBoolean(Guild guild, String key) {
		return getBoolean(guild.getId(), key);
	}
	
	@Nullable
	/**
	 * @param guild
	 * @param key
	 * @return FALSE IF NOT PRESENT!
	 */
	public static boolean getBoolean(String guild, String key) {
		final Document doc = MongoManager.getDocumentInCollection(collection, "guildid", guild);
		if (doc == null || !doc.containsKey(key)) return false;
		return doc.getBoolean(key);
	}
	
	@Nonnull
	public static boolean getBoolean(Guild guild, String key, boolean defaultValue) {
		return getBoolean(guild.getId(), key, defaultValue);
	}
	
	@Nonnull
	public static boolean getBoolean(String guild, String key, boolean defaultValue) {
		final Document doc = MongoManager.getDocumentInCollection(collection, "guildid", guild);
		if (doc != null && doc.containsKey(key))
			return doc.getBoolean(key);
		else {
			MongoManager.updateValue(collection, new BasicDBObject().append("guildid", guild), new Document(key, defaultValue));
			return defaultValue;
		}
	}
	
	@Nonnull
	public static <T> List<T> getList(Guild guild, String key, List<T> defaultValue, Class<T> clazz) {
		return getList(guild.getId(), key, clazz);
	}
	
	@Nonnull
	public static <T> List<T> getList(String guild, String key, List<T> defaultValue, Class<T> clazz) {
		final Document doc = MongoManager.getDocumentInCollection(collection, "guildid", guild);
		if (doc != null && doc.containsKey(key))
			return doc.getList(key, clazz);
		else {
			MongoManager.updateValue(collection, new BasicDBObject().append("guildid", guild), new Document(key, defaultValue));
			return defaultValue;
		}
	}
	
	@Nullable
	public static <T> List<T> getList(Guild guild, String key, Class<T> clazz) {
		return getList(guild.getIdLong(), key, clazz);
	}
	
	@Nullable
	public static <T> List<T> getList(long guild, String key, Class<T> clazz) {
		final Document doc = collection.find(Filters.eq("guildid", guild)).first();
		if (doc == null) return null;
		return doc.getList(key, clazz);
	}
	
	public static void save(Guild guild, String key, Object value) {
		save(guild.getIdLong(), key, value);
	}
	
	public static void save(long guild, String key, Object value) {
		MongoManager.updateValue(collection, new BasicDBObject().append("guildid", guild), new Document().append(key, value));
	}
	
	public static <T> void saveList(Guild guild, String key, List<T> value) {
		saveList(guild.getIdLong(), key, value);
	}
	
	public static <T> void saveList(long guild, String key, List<T> value) {
		collection.updateOne(Filters.eq("guildid", guild), new Document("$set", new Document(key, value)));
	}
	
	@Nonnull
	public static boolean isPremium(Guild guild) {
		return isPremium(guild.getId());
	}
	
	@Nonnull
	public static boolean isPremium(long guild) {
		final Document doc = getGuildConfig;
		if (doc != null && doc.containsKey("isPremium")) return doc.getBoolean("isPremium");
		else {
			MongoManager.updateValue(collection, new BasicDBObject().append("guildid", guild), new Document("isPremium", false));
			return false;
		}
	}
	
	public static void remove(String guild, String key) {
		MongoManager.removeValue(collection, new BasicDBObject().append("guildid", guild), key);
	}
	
	public static void remove(Guild guild, String key) {
		remove(guild.getId(), key);
	}
	
	public static Document getGuildConfig(BlackGuild guild) {
		return getGuildConfig(guild.getIdLong());
	}
	
	public static Document getGuildConfig(long guildid) {
		return collection.find(Filters.eq("guildid", guildid)).first();
	}
}
