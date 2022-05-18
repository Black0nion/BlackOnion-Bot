package com.github.black0nion.blackonionbot.api.sessions;

import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import com.github.black0nion.blackonionbot.oauth.OAuthUtils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.annotation.Nullable;
import java.util.InputMismatchException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

public abstract sealed class GenericSession permits RestSession, WebSocketSession {

	public static final String SESSIONID_REGEX = "[a-zA-Z\\d]{69}";
	@Nullable
	protected String sessionId;
	protected DiscordUser user;

	public GenericSession(final String sessionId) throws InputMismatchException {
		this.loginToSession(sessionId);
	}


	public DiscordUser getUser() {
		return this.user;
	}

	@Nullable
	public String getSessionId() {
		return this.sessionId;
	}

	protected final void loginToSession(final String sessionId) throws InputMismatchException, NullPointerException {
		try {
			this.user = login.loginToSession(sessionId);
		} catch (ExecutionException e) {
			throw e.getCause() instanceof RuntimeException ? (RuntimeException) e.getCause() : new RuntimeException(e.getCause());
		}
	}

	public final void logout() throws InputMismatchException, NullPointerException {
		login.logoutFromSession(this.sessionId);
		this.sessionId = null;
		this.user = null;
	}

	private static ILogin login = IMongoLogin.INSTANCE;
	public static void setLogin(ILogin login) {
		GenericSession.login = login;
	}
	public static ILogin getLogin() {
		return login;
	}

	public static class IMongoLogin implements ILogin {
		protected static final IMongoLogin INSTANCE = new IMongoLogin();
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
			final String newSessionId = GenericSession.generateSessionId();
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

	private static final int TARGET_STRING_LENGTH = 69;
	private static final int LEFT_LIMIT_1 = 'A';
	private static final int LEFT_LIMIT_2 = 'a';
	private static final int LEFT_LIMIT_3 = '0';
	private static final int RIGHT_LIMIT_1 = 'Z';
	private static final int RIGHT_LIMIT_2 = 'z';
	private static final int RIGHT_LIMIT_3 = '9';
	public static String generateSessionId() {
		final String generatedId = ThreadLocalRandom.current().ints(LEFT_LIMIT_3, RIGHT_LIMIT_2 + 1)
			.filter(i ->  ((i <= RIGHT_LIMIT_1 && i >= LEFT_LIMIT_1)
				|| (i <= RIGHT_LIMIT_2 && i >= LEFT_LIMIT_2)
				|| (i <= RIGHT_LIMIT_3 && i >= LEFT_LIMIT_3)))
			.limit(TARGET_STRING_LENGTH)
			.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
			.toString();

		// check if the database contains an entry for this session id, very unlikely
		if (getLogin().isIdOccupied(generatedId)) return generateSessionId();

		return generatedId;
	}
}