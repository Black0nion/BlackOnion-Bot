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
    public final <T> T gOS(final String key, final T value, final T defaultValue) {
	if (value == null) {
	    save(key, defaultValue);
	}
	return (value != null ? value : defaultValue);
    }

	@Nullable
    public Document getConfig() {
	return getCollection().find(getIdentifier()).first();
    }

	/**
	 * @return an arraylist containing all configs, empty if none present
	 */
    @Nonnull
    public List<Document> getAllConfigs() {
	return getCollection().find().into(new ArrayList<>());
    }

    public <T> void saveList(final String key, final List<T> value) {
	save(new Document(key, value));
    }

    public <T> T get(final String key, final Class<T> clazz) {
	return getConfig().get(key, clazz);
    }

    public <T> void save(final String key, final T value) {
	save(new Document(key, value));
    }

    private void save(final Document doc) {
	if (getCollection().find(getIdentifier()).first() == null) {
	    final Document newDoc = getIdentifier();
	    newDoc.putAll(doc);
	    getCollection().insertOne(newDoc);
	} else {
	    getCollection().updateOne(getIdentifier(), new Document("$set", doc));
	}
    }

    public void clear(final String... keys) {
	final Document doc = new Document();
	for (final String key : keys) {
	    doc.put(key, "");
	}
	clear(doc);
    }

    public void clear(final Document doc) {
	getCollection().updateOne(getIdentifier(), new Document("$unset", doc));
    }
}