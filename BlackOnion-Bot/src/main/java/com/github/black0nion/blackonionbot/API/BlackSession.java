package com.github.black0nion.blackonionbot.API;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.utils.DiscordUser;
import com.github.black0nion.blackonionbot.utils.Trio;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

/**
 * Wrapper class for websocket sessions.
 *
 * @author _SIM_
 */
public class BlackSession {

    public static final MongoCollection<Document> collection = MongoDB.botDatabase.getCollection("dashboard-sessions");

    private String sessionId;
    private DiscordUser user;

    public BlackSession() {
	this.createSession();
    }

    public BlackSession(final String sessionId) {
	this.sessionId = sessionId;
    }

    public void createSession() {
	this.sessionId = generateSessionId();
	collection.insertOne(new Document().append("sessionid", this.sessionId));
    }

    /**
     * The websocket will connect and then provide a session id to login to. If the
     * websocket connection isn't logged in, use {@link #loginWithDiscord(String)}
     *
     * Workflow:
     *
     * - gets session id assigned on connect to WS
     *
     * - client logs in using that session
     *
     * - old session data gets erased (it will only contain default stuff)
     *
     * @param sessionId
     * @return
     */
    public boolean loginToSession(final String sessionId) {
	if (collection.find(Filters.eq("sessionid", sessionId)).first() != null) {
	    collection.deleteOne(Filters.eq("sessionid", this.sessionId));
	    this.sessionId = sessionId;
	    return true;
	} else return false;
    }

    /**
     * call once to generate token from code and save that shit, only on first login
     * with discord, on reconnect on the same PC (session) use
     * {@link #loginToSession(String)}!
     *
     * Workflow:
     *
     * - connect to websocket
     *
     * - gets session id assigned & saved to database on initialize
     *
     * - after this method, the entry with the sessionid gets updated
     *
     * - when the client comes back, it logs in using that session id
     *
     * @param code the code discord gave you
     * @return if it worked
     */
    public boolean loginWithDiscord(final String code) {
	try {
	    final Trio<String, String, Integer> response = OAuthUtils.getTokensFromCode(code);
	    if (response == null) return false;
	    else {
		final String accessToken = response.getFirst();
		final String refreshToken = response.getSecond();
		final int expiresIn = response.getThird();
		final JSONObject userinfo = OAuthUtils.getUserInfoFromToken(accessToken);
		this.user = new DiscordUser(userinfo.getLong("id"), userinfo.getString("username"), userinfo.getString("avatar"), userinfo.getString("discriminator"), userinfo.getString("locale"), userinfo.getBoolean("mfa_enabled"));
		final Bson filter = Filters.eq("sessionid", this.sessionId);
		if (collection.find(filter).first() != null) {
		    collection.updateOne(filter, new Document("$set", new Document().append("access_token", accessToken).append("refresh_token", refreshToken).append("expires_in", expiresIn)));
		} else {
		    collection.insertOne(new Document().append("access_token", accessToken).append("refresh_token", refreshToken).append("expires_in", expiresIn));
		}
		return true;
	    }
	} catch (final Exception e) {
	    e.printStackTrace();
	    return false;
	}
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