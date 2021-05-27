package com.github.black0nion.blackonionbot.mongodb;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.github.black0nion.blackonionbot.Logger;
import com.github.black0nion.blackonionbot.misc.LogMode;
import com.github.black0nion.blackonionbot.misc.LogOrigin;
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
	public static boolean connect(String connectionString) {
		final long start = System.currentTimeMillis();
		client = new MongoClient(new MongoClientURI(connectionString));
		final long end = System.currentTimeMillis();
		Logger.log(LogMode.INFORMATION, LogOrigin.MONGODB, "Successfully connected to " + client.getConnectPoint() + " in " + (end - start) + " ms.");
		try {
			client.isLocked();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean connect(String ip, String db, String userName, String password) {
		return connect(ip, "27017", db, userName, password, 20000);
	}
	
	@SuppressWarnings("deprecation")
	public static boolean connect(String ip, String port, String db, String userName, String password, int timeout) {
		final long start = System.currentTimeMillis();
		client = new MongoClient(new ServerAddress(ip, Integer.parseInt(port)), MongoCredential.createCredential(userName, db, password.toCharArray()), MongoClientOptions.builder().connectTimeout(timeout).build());
		final long end = System.currentTimeMillis();
		try {
			client.isLocked();
			Logger.log(LogMode.INFORMATION, LogOrigin.MONGODB, "Successfully connected to " + client.getConnectPoint() + " in " + (end - start) + " ms.");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void disconnect() {
		client.close();
	}
	
	public static MongoDatabase getDatabase(String key) {
		return client.getDatabase(key);
	}
	
	public static MongoCollection<Document> getCollection(String key, MongoDatabase db) {
		return db.getCollection(key);
	}
	
	public static List<Document> getDocumentsInCollection(String key, MongoCollection<Document> col) {
		List<Document> docs = new ArrayList<>();
		try (MongoCursor<Document> cursor = col.find().iterator()) {
		    while (cursor.hasNext()) {
		        docs.add(cursor.next());
		    }
		}
		return docs;
	}
	
	public static Document getDocumentInCollection(MongoCollection<Document> col, String key, String value) {
		return col.find(Filters.eq(key, value)).first();
	}
	
	public static void insertOne(MongoCollection<Document> collection, Document document) {
		collection.insertOne(document);
	}
	
	public static void insertMany(MongoCollection<Document> collection, List<Document> documents) {
		collection.insertMany(documents);
	}
	
	public static void updateOne(MongoCollection<Document> collection, BasicDBObject query, BasicDBObject updatedValue) {
		BasicDBObject updateObject = new BasicDBObject();
		updateObject.put("$set", updatedValue);
		
		collection.updateOne(query, updateObject);
	}
	
	public static void updateMany(MongoCollection<Document> collection, BasicDBObject query, BasicDBObject updatedValue) {
		BasicDBObject updateObject = new BasicDBObject();
		updateObject.put("$set", updatedValue);
		
		collection.updateMany(query, updateObject);
	}
	
	public static void updateValue(MongoCollection<Document> collection, BasicDBObject query, Document updatedValue) {	
		final Document tempDoc = collection.find(query).first();
		if (tempDoc != null)
			collection.updateOne(query, new BasicDBObject().append("$set", updatedValue));
		else {
			updatedValue.putAll(query);
			collection.insertOne(updatedValue);	
		}
	}
	
	public static void removeValue(MongoCollection<Document> collection, BasicDBObject query, String key) {
		BasicDBObject updateObject = new BasicDBObject();
		updateObject.put("$unset", new Document(key, 1));
		
		collection.updateOne(query, updateObject);
	}
	
	public static void removeValue(MongoCollection<Document> collection, BasicDBObject query, Document remove) {
		BasicDBObject updateObject = new BasicDBObject();
		updateObject.put("$unset", remove);
		
		collection.updateOne(query, updateObject);
	}
}
