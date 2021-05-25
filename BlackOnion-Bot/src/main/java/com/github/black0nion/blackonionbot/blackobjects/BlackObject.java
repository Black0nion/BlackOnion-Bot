package com.github.black0nion.blackonionbot.blackobjects;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;

public abstract class BlackObject {
		
	abstract Bson getFilter();
	
	abstract MongoCollection<Document> getCollection();
	
	public final <T> T gOS(String key, T value, T defaultValue) {
		if (value == null) save(key, defaultValue);
		return (value != null ? value : defaultValue);
	}
	
	public final <T> T gOD(T value, T defaultValue) {
		return (value != null ? value : defaultValue);
	}
	
	public Document getConfig() {
		return getCollection().find(getFilter()).first();
	}
	
	@Nonnull
	/**
	 * @return an arraylist containing all configs, empty if none present
	 */
	public List<Document> getAllConfigs() {
		return getCollection().find().into(new ArrayList<>());
	}
	
	@Nullable
	public <T> List<T> getList(String key, Class<T> clazz) {
		final Document doc = getConfig();
		if (doc == null) return null;
		return doc.getList(key, clazz);
	}
	
	public <T> void saveList(String key, List<T> value) {
		save(new Document("$set", new Document(key, value)));
	}
	
	public <T> T get(String key, Class<T> clazz) {
		return getConfig().get(key, clazz);
	}
	
	public <T> void save(String key, T value) {
		save(new Document(key, value));
	}
	
	private void save(Document doc) {
		getCollection().updateOne(getFilter(), new Document("$set", doc));
	}
}