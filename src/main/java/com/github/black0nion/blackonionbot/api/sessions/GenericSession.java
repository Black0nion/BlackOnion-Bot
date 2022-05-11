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

	protected GenericSession(final String sessionId) throws InputMismatchException {
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

	private static final LoginFactory.ILogin login = LoginFactory.getImpl();

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
		if (LoginFactory.getImpl().isIdOccupied(generatedId)) return generateSessionId();

		return generatedId;
	}
}