package com.github.black0nion.blackonionbot.mongodb;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.client.*;
import org.bson.Document;

import com.mongodb.client.model.Filters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoManager {

  public static MongoClient client;

  private static final Logger logger = LoggerFactory.getLogger(MongoManager.class);

  public static void connect(final String connectionStringRaw) {
    logger.info("Connecting to {}...", connectionStringRaw);
    final long start = System.currentTimeMillis();
    ConnectionString connectionString = new ConnectionString(connectionStringRaw);
    client = MongoClients.create(connectionString);
    client.startSession();
    final long end = System.currentTimeMillis();
    logger.info("Connected to MongoDB in {}ms", end - start);
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

  public static List<Document> getDocumentsInCollection(final String key,
      final MongoCollection<Document> col) {
    final List<Document> docs = new ArrayList<>();
    try (MongoCursor<Document> cursor = col.find().iterator()) {
      while (cursor.hasNext()) {
        docs.add(cursor.next());
      }
    }
    return docs;
  }

  public static Document getDocumentInCollection(final MongoCollection<Document> col,
      final String key, final String value) {
    return col.find(Filters.eq(key, value)).first();
  }

  public static void insertOne(final MongoCollection<Document> collection,
      final Document document) {
    collection.insertOne(document);
  }

  public static void insertMany(final MongoCollection<Document> collection,
      final List<Document> documents) {
    collection.insertMany(documents);
  }

  public static void updateOne(final MongoCollection<Document> collection,
      final BasicDBObject query, final BasicDBObject updatedValue) {
    final BasicDBObject updateObject = new BasicDBObject();
    updateObject.put("$set", updatedValue);

    collection.updateOne(query, updateObject);
  }

  public static void updateMany(final MongoCollection<Document> collection,
      final BasicDBObject query, final BasicDBObject updatedValue) {
    final BasicDBObject updateObject = new BasicDBObject();
    updateObject.put("$set", updatedValue);

    collection.updateMany(query, updateObject);
  }

  public static void updateValue(final MongoCollection<Document> collection,
      final BasicDBObject query, final Document updatedValue) {
    final Document tempDoc = collection.find(query).first();
    if (tempDoc != null) {
      collection.updateOne(query, new BasicDBObject().append("$set", updatedValue));
    } else {
      updatedValue.putAll(query);
      collection.insertOne(updatedValue);
    }
  }

  public static void removeValue(final MongoCollection<Document> collection,
      final BasicDBObject query, final String key) {
    final BasicDBObject updateObject = new BasicDBObject();
    updateObject.put("$unset", new Document(key, 1));

    collection.updateOne(query, updateObject);
  }

  public static void removeValue(final MongoCollection<Document> collection,
      final BasicDBObject query, final Document remove) {
    final BasicDBObject updateObject = new BasicDBObject();
    updateObject.put("$unset", remove);

    collection.updateOne(query, updateObject);
  }
}
