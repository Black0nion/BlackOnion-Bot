package com.github.black0nion.blackonionbot.api.sessions;

import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import com.github.black0nion.blackonionbot.oauth.OAuthUtils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.InputMismatchException;
import java.util.concurrent.ExecutionException;

public final class LoginFactory {

	private static ILoginFactory instance;
	private static ILoginFactory lastInstance;

	public static void setInstance(ILoginFactory instance) {
		LoginFactory.instance = instance;
	}

	private static ILogin loginInstance;

	private static void checkExists() {
		if (instance == null) {
			instance = MongoLogin::new;
			loginInstance = instance.createImpl();
		} else if (lastInstance != instance) {
			lastInstance = instance;
			loginInstance = instance.createImpl();
		}
	}

	public static ILogin getImpl() {
		checkExists();
		return loginInstance;
	}

	public interface ILoginFactory {
		ILogin createImpl();
	}

	public interface ILogin {
		DiscordUser loginToSession(String sessionId) throws ExecutionException, InputMismatchException, NullPointerException;
		void logoutFromSession(String sessionId) throws InputMismatchException, NullPointerException;

		/**
		 * @return the session id of the newly generated session
		 */
		String createSession(String accessToken, String refreshToken, int expiresIn);

		default boolean isIdOccupied(String sessionId) {
			return false;
		}
	}

	private static class MongoLogin implements LoginFactory.ILogin {
		public static final String SESSIONID = "sessionid";

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

			return OAuthUtils.getUserWithToken(doc.getString("access_token"), doc.getString("refresh_token"));
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
			final Document find = getCollection().find(Filters.and(Filters.eq("access_token", accessToken), Filters.eq("refresh_token", refreshToken), Filters.exists("sessionid"))).first();
			if (find != null) return find.getString(SESSIONID);
			final String newSessionId = AbstractSession.generateSessionId();
			getCollection().insertOne(new Document()
					.append(SESSIONID, newSessionId)
					.append("access_token", accessToken)
					.append("refresh_token", refreshToken)
					.append("expires_in", expiresIn));
			return newSessionId;
		}

		@Override
		public boolean isIdOccupied(String sessionId) {
			return getCollection().find(Filters.eq(SESSIONID, sessionId)).first() != null;
		}
	}
}