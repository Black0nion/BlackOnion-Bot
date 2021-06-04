package com.github.black0nion.blackonionbot.blackobjects;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

public abstract class BlackObject {
		
	abstract Document getIdentifier();
	
	abstract MongoCollection<Document> getCollection();
	
	@Nonnull
	public final <T> T gOS(String key, T value, T defaultValue) {
		if (value == null) save(key, defaultValue);
		return (value != null ? value : defaultValue);
	}
	
	@Nonnull
	public final <T> T gOD(T value, T defaultValue) {
		return (value != null ? value : defaultValue);
	}
	
	@Nullable
	public Document getConfig() {
		return getCollection().find(getIdentifier()).first();
	}
	
	@Nonnull
	/**
	 * @return an arraylist containing all configs, empty if none present
	 */
	public List<Document> getAllConfigs() {
		return getCollection().find().into(new ArrayList<>());
	}
	
	public <T> void saveList(String key, List<T> value) {
		save(new Document(key, value));
	}
	
	public <T> T get(String key, Class<T> clazz) {
		return getConfig().get(key, clazz);
	}
	
	public <T> void save(String key, T value) {
		save(new Document(key, value));
	}
	
	private void save(Document doc) {
		if (getCollection().find(getIdentifier()).first() == null) {
			Document newDoc = getIdentifier();
			newDoc.putAll(doc);
			getCollection().insertOne(newDoc);
		} else
			getCollection().updateOne(getIdentifier(), new Document("$set", doc));
	}
	
	public void clear(String... keys) {
		Document doc = new Document();
		for (String key : keys) doc.put(key, "");
		clear(doc);
	}
	
	public void clear(Document doc) {
		getCollection().updateOne(getIdentifier(), new Document("$unset", doc));
	}
}