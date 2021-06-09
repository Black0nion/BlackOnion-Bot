package com.github.black0nion.blackonionbot.mongodb;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.github.black0nion.blackonionbot.misc.LogMode;
import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.systems.logging.Logger;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class MongoManager {
	
	public static MongoClient client;
	
	@SuppressWarnings("deprecation")
	public static boolean connect(final String connectionString) {
		final long start = System.currentTimeMillis();
		client = new MongoClient(new MongoClientURI(connectionString));
		final long end = System.currentTimeMillis();
		Logger.log(LogMode.INFORMATION, LogOrigin.MONGODB, "Successfully connected to " + client.getConnectPoint() + " in " + (end - start) + " ms.");
		try {
			client.isLocked();
			return true;
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean connect(final String ip, final String db, final String userName, final String password) {
		return connect(ip, "27017", db, userName, password, 20000);
	}
	
	@SuppressWarnings("deprecation")
	public static boolean connect(final String ip, final String port, final String db, final String userName, final String password, final int timeout) {
		final long start = System.currentTimeMillis();
		client = new MongoClient(new ServerAddress(ip, Integer.parseInt(port)), MongoCredential.createCredential(userName, db, password.toCharArray()), MongoClientOptions.builder().connectTimeout(timeout).build());
		final long end = System.currentTimeMillis();
		try {
			client.isLocked();
			Logger.log(LogMode.INFORMATION, LogOrigin.MONGODB, "Successfully connected to " + client.getConnectPoint() + " in " + (end - start) + " ms.");
			return true;
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void disconnect() {
		client.close();
	}
	
	public static MongoDatabase getDatabase(final String key) {
		return client.getDatabase(key);
	}
	
	public static MongoCollection<Document> getCollection(final String key, final MongoDatabase db) {
		return db.getCollection(key);
	}
	
	public static List<Document> getDocumentsInCollection(final String key, final MongoCollection<Document> col) {
		final List<Document> docs = new ArrayList<>();
		try (MongoCursor<Document> cursor = col.find().iterator()) {
		    while (cursor.hasNext())
				docs.add(cursor.next());
		}
		return docs;
	}
	
	public static Document getDocumentInCollection(final MongoCollection<Document> col, final String key, final String value) {
		return col.find(Filters.eq(key, value)).first();
	}
	
	public static void insertOne(final MongoCollection<Document> collection, final Document document) {
		collection.insertOne(document);
	}
	
	public static void insertMany(final MongoCollection<Document> collection, final List<Document> documents) {
		collection.insertMany(documents);
	}
	
	public static void updateOne(final MongoCollection<Document> collection, final BasicDBObject query, final BasicDBObject updatedValue) {
		final BasicDBObject updateObject = new BasicDBObject();
		updateObject.put("$set", updatedValue);
		
		collection.updateOne(query, updateObject);
	}
	
	public static void updateMany(final MongoCollection<Document> collection, final BasicDBObject query, final BasicDBObject updatedValue) {
		final BasicDBObject updateObject = new BasicDBObject();
		updateObject.put("$set", updatedValue);
		
		collection.updateMany(query, updateObject);
	}
	
	public static void updateValue(final MongoCollection<Document> collection, final BasicDBObject query, final Document updatedValue) {	
		final Document tempDoc = collection.find(query).first();
		if (tempDoc != null)
			collection.updateOne(query, new BasicDBObject().append("$set", updatedValue));
		else {
			updatedValue.putAll(query);
			collection.insertOne(updatedValue);	
		}
	}
	
	public static void removeValue(final MongoCollection<Document> collection, final BasicDBObject query, final String key) {
		final BasicDBObject updateObject = new BasicDBObject();
		updateObject.put("$unset", new Document(key, 1));
		
		collection.updateOne(query, updateObject);
	}
	
	public static void removeValue(final MongoCollection<Document> collection, final BasicDBObject query, final Document remove) {
		final BasicDBObject updateObject = new BasicDBObject();
		updateObject.put("$unset", remove);
		
		collection.updateOne(query, updateObject);
	}
}
