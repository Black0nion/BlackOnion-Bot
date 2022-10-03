package com.github.black0nion.blackonionbot.rest.sessions;

import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import com.github.black0nion.blackonionbot.oauth.OAuthHandler;
import com.github.black0nion.blackonionbot.oauth.api.SessionHandler;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.InputMismatchException;
import java.util.concurrent.ExecutionException;

public class MongoLogin implements SessionHandler {

	public static final String ACCESS_TOKEN = "access_token";
	public static final String REFRESH_TOKEN = "refresh_token";
	/**
	 * Lazy init to allow the unit tests to set the impl before trying to get the collection
	 */
	private MongoCollection<Document> collection;

	public MongoCollection<Document> getCollection() {
		return collection == null ? collection = MongoDB.getInstance().getDatabase().getCollection("sessions") : collection;
	}

	@Override
	public DiscordUser loginToSession(String sessionId) throws ExecutionException, InputMismatchException, NullPointerException {
		final Document doc = getCollection().find(Filters.eq(SESSIONID, sessionId)).first();

		if (doc == null) throw new NullPointerException("Session id not found");

		return OAuthHandler.getUserWithToken(doc.getString(ACCESS_TOKEN), doc.getString(REFRESH_TOKEN));
	}

	@Override
	public void logoutFromSession(String sessionId) throws InputMismatchException, NullPointerException {
		Bson filter = Filters.eq(SESSIONID, sessionId);
		final Document doc = getCollection().find(filter).first();

		if (doc == null) {
			throw new NullPointerException("Session id not found");
		} else {
			getCollection().deleteOne(filter);
		}
	}

	@Override
	public String createSession(String accessToken, String refreshToken, int expiresIn) {
		final Document find = getCollection().find(Filters.and(Filters.eq(ACCESS_TOKEN, accessToken), Filters.eq(REFRESH_TOKEN, refreshToken), Filters.exists("sessionid"))).first();
		if (find != null) return find.getString(SESSIONID);
		final String newSessionId = AbstractSession.generateSessionId();
		getCollection().insertOne(new Document()
				.append(SESSIONID, newSessionId)
				.append(ACCESS_TOKEN, accessToken)
				.append(REFRESH_TOKEN, refreshToken)
				.append("expires_in", expiresIn));
		return newSessionId;
	}

	@Override
	public boolean isIdOccupied(String sessionId) {
		return getCollection().find(Filters.eq(SESSIONID, sessionId)).first() != null;
	}
}
