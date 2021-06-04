package com.github.black0nion.blackonionbot.systems.dashboard;

import javax.annotation.Nullable;

import org.bson.Document;

import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.mongodb.MongoManager;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;

import spark.Session;

public class SessionManager {
	
	private static MongoCollection<Document> collection;
	
	@Reloadable("sessionmanager")
	public static void init() {
		collection = MongoDB.botDatabase.getCollection("dashboard-sessions");
		
		for (Document doc : collection.find()) {
			if (!doc.containsKey("sessionid"))
				System.out.println("Error! Object with id " + doc.getObjectId("_id").toHexString() + " is invalid!");
			else if (doc.containsKey("sessionid") && doc.containsKey("access_token") && doc.containsKey("refresh_token")) {
				final DashboardSessionInformation info = DashboardSessionInformation.from(doc.getString("sessionid"), doc.getString("access_token"), doc.getString("refresh_token"));
				if (info.getUser() == null) collection.deleteOne(new BasicDBObject().append("sessionid", doc.getString("sessionid")));
			} else
				DashboardSessionInformation.from(doc.getString("sessionid"));
		}
	}
	
	public static DashboardSessionInformation generateSession(Session session) {
		final Document doc = MongoManager.getDocumentInCollection(collection, "sessionid", session.id());
		
		if (doc == null) {
			MongoManager.insertOne(collection, new Document().append("sessionid", session.id()));
			return DashboardSessionInformation.from(session.id());
		} else if (doc.containsKey("access_token") && doc.containsKey("refresh_token")) {
			return DashboardSessionInformation.from(session.id(), doc.getString("access_token"), doc.getString("refresh_token"));
		} else {
			return DashboardSessionInformation.from(session.id());
		}
	}
	
	public static DashboardSessionInformation generateSession(Session session, String accessToken, String refreshToken) {
		if (MongoManager.getDocumentInCollection(collection, "sessionid", session.id()) == null)
			MongoManager.insertOne(collection, new Document().append("sessionid", session.id()));
		else
			MongoManager.updateOne(collection, new BasicDBObject("sessionid", session.id()), new BasicDBObject()
					.append("sessionid", session.id())
					.append("access_token", accessToken)
					.append("refresh_token", refreshToken));
		return DashboardSessionInformation.from(session.id(), accessToken, refreshToken);
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
