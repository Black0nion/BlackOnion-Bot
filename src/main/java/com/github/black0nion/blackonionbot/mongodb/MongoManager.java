package com.github.black0nion.blackonionbot.mongodb;

import com.github.black0nion.blackonionbot.config.api.Config;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoManager {

	private MongoManager() {}


	private static MongoClient client;

	private static final Logger logger = LoggerFactory.getLogger(MongoManager.class);

	public static void connect(final String connectionStringRaw, Config config) {
		logger.info("Connecting to {}...", connectionStringRaw);
		final long start = System.currentTimeMillis();
		ConnectionString connectionString = new ConnectionString(connectionStringRaw);
		client = MongoClients.create(connectionString);
		client.startSession();
		final long end = System.currentTimeMillis();
		logger.info("Connected to MongoDB in {}ms", end - start);
		new MongoDB(config);
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

	public static void insertOne(final MongoCollection<Document> collection, final Document document) {
		collection.insertOne(document);
	}
}
