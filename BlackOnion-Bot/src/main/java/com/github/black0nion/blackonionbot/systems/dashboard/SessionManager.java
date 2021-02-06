package com.github.black0nion.blackonionbot.systems.dashboard;

import javax.annotation.Nullable;

import org.bson.Document;

import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.mongodb.MongoManager;
import com.mongodb.client.MongoCollection;

import spark.Session;

public class SessionManager {
	
	private static MongoCollection<Document> collection;
	
	public static void init() {
		collection = MongoDB.botDatabase.getCollection("dashboard-sessions");
	}
	
	public static Session genereteSession(Session session) {
		if (MongoManager.getDocumentInCollection(collection, "sessionid", session.id()) == null)
			MongoManager.insertOne(collection, new Document().append("sessionid", session.id()));
		return session;
	}
	
	public static Session generateSession(Session session, String accessToken, String refreshToken) {
		if (MongoManager.getDocumentInCollection(collection, "sessionid", session.id()) == null)
			MongoManager.insertOne(collection, new Document().append("sessionid", session.id()).append("accesstoken", accessToken).append("refreshtoken", refreshToken));
		return session;
	}
	
	@Nullable
	public static DashboardSessionInformation getSessionInformation(Session session) {
		return getSessionInformation(session.id());
	}
	
	@Nullable
	public static DashboardSessionInformation getSessionInformation(String sessionId) {
		return DashboardSessionInformation.get(sessionId);
	}
}
