package com.github.black0nion.blackonionbot.API;

import com.github.black0nion.blackonionbot.API.impl.post.Login;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import com.github.black0nion.blackonionbot.oauth.OAuthUtils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import javax.annotation.Nullable;

/**
 * Wrapper class for websocket sessions.
 *
 * @author _SIM_
 */
public class BlackSession {

	public static final MongoCollection<Document> collection = MongoDB.DATABASE.getCollection("dashboard-sessions");

	@Nullable
	private String sessionId;
	private DiscordUser user;

	public BlackSession(final String sessionId) throws IllegalArgumentException {
		if (!this.loginToSession(sessionId)) throw new IllegalArgumentException("Invalid session id");
	}

	/**
	 * The websocket will connect and then provide a session id to login to. If the
	 * websocket connection isn't logged in, use {@link Login#loginWithDiscord(String)}
	 * <p>
	 * Workflow:
	 * <p>
	 * - client gets a session id using {@link com.github.black0nion.blackonionbot.API.impl.post.Login#loginWithDiscord(String)}
	 * - client links the session id to the websocket using this method
	 *
	 */
	public boolean loginToSession(final String sessionId) {
		final Document doc = collection.find(Filters.eq("sessionid", sessionId)).first();
		if (doc != null) {
			this.sessionId = sessionId;
			this.user = OAuthUtils.getUserWithToken(doc.getString("access_token"));
			return true;
		} else return false;
	}

	public static String generateSessionId() {
		final int leftLimit1 = 65; // letter 'A'
		final int rightLimit1 = 90; // letter 'B'
		final int leftLimit2 = 97; // letter 'a'
		final int rightLimit2 = 122; // letter 'z'
		final int leftLimit3 = 48; // 0
		final int rightLimit3 = 57; // 9

		final int targetStringLength = 69;

		final String generatedId = Bot.random.ints(leftLimit3, rightLimit2 + 1).filter(i -> ((i <= rightLimit1 && i >= leftLimit1) || (i <= rightLimit2 && i >= leftLimit2) || (i <= rightLimit3 && i >= leftLimit3))).limit(targetStringLength + 2).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

		// check if the database contains a entry for this session id, very unlikely
		if (collection.find(Filters.eq("sessionid", generatedId)).first() != null) return generateSessionId();

		return generatedId;
	}

	public DiscordUser getUser() {
		return this.user;
	}

	public final String getSessionId() {
		return this.sessionId;
	}
}