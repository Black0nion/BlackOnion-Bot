package com.github.black0nion.blackonionbot.systems.dashboard;

import static com.github.black0nion.blackonionbot.systems.dashboard.DiscordLogin.error;
import static com.github.black0nion.blackonionbot.systems.dashboard.DiscordLogin.SessionError.DISCORD_ERROR;
import static com.github.black0nion.blackonionbot.systems.dashboard.DiscordLogin.SessionError.EXCEPTION;
import static com.github.black0nion.blackonionbot.systems.dashboard.DiscordLogin.SessionError.INVALID_DISCORD_CODE;
import static com.github.black0nion.blackonionbot.systems.dashboard.DiscordLogin.SessionError.INVALID_SCOPES;
import static com.github.black0nion.blackonionbot.systems.dashboard.DiscordLogin.SessionError.SESSION_NOT_CREATED;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;

import org.bson.Document;
import org.eclipse.jetty.websocket.api.CloseStatus;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.SuspendToken;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.API.impl.DashboardWebsocket;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.systems.logging.Logger;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

/**
 * @author _SIM_
 */
public class BlackSession implements org.eclipse.jetty.websocket.api.Session {

    public static final MongoCollection<Document> collection = MongoDB.botDatabase.getCollection("dashboard-sessions");

    public BlackSession(final Session session) {
	this.session = session;
	this.sessionId = null;
    }

    private final Session session;
    @SuppressWarnings("unused")
    private int heartbeats;
    private String sessionId;

    public boolean createSession() {
	this.sessionId = generateSessionId();
	// TODO: login
	collection.insertOne(new Document().append("sessionid", this.sessionId));
	return true;
    }

    public boolean loginToSession(final String sessionId) {
	if (collection.find(Filters.eq("sessionid", sessionId)).first() != null) {
	    this.sessionId = sessionId;
	    return true;
	} else return false;
    }

    /**
     * call once to generate token from code and save that shit, only on first
     * login!
     *
     * @param code the code discord gave you
     * @return if it worked
     */
    public DiscordLogin loginWithDiscord(final String code) {
	try {
	    final boolean createSession = createSession();
	    if (!createSession) {
		Logger.logError("Login with Discord failed! Code: " + code, LogOrigin.DASHBOARD);
		return error(SESSION_NOT_CREATED);
	    }
	    final JSONObject response = new JSONObject(Utils.getTokenFromCode(code).getBody());
	    if (response.has("error")) return error(INVALID_DISCORD_CODE);
	    else {
		final boolean hasScopes = Arrays.asList(String.join(" ", response.getString("scope"))).containsAll(DashboardWebsocket.requiredScopes);
		if (!hasScopes) return error(INVALID_SCOPES);
		if (!response.keySet().containsAll(Arrays.asList("access_token", "refresh_token", "expires_in"))) return error(DISCORD_ERROR);
		final String accessToken = response.getString("access_token");
		final String refreshToken = response.getString("refresh_token");
		final int expiresIn = response.getInt("expires_in");
		final JSONObject userinfo = Utils.getUserInfoFromToken(accessToken);
		final DiscordLogin login = DiscordLogin.success(userinfo);
		collection.updateOne(Filters.eq("sessionid", sessionId), new Document().append("access_token", accessToken).append("refresh_token", refreshToken).append("expires_in", expiresIn));
		return login;
	    }
	} catch (final Exception e) {
	    e.printStackTrace();
	    return error(EXCEPTION);
	}
    }

    /**
     * @return the sessionId of the current session
     */
    public String getSessionId() {
	return sessionId;
    }

    @Override
    public void close() {
	session.close();
    }

    @Override
    public void close(final CloseStatus closeStatus) {
	session.close(closeStatus);
    }

    @Override
    public void close(final int statusCode, final String reason) {
	Logger.logInfo("IP " + session.getRemote().getInetSocketAddress().getAddress().getHostAddress() + " got yeeted out.", LogOrigin.DASHBOARD);
	session.close(statusCode, reason);
    }

    @Override
    public void disconnect() throws IOException {
	Logger.logInfo("IP " + session.getRemote().getInetSocketAddress().getAddress().getHostAddress() + " got yeeted out.", LogOrigin.DASHBOARD);
	session.disconnect();
    }

    @Override
    public long getIdleTimeout() {
	return session.getIdleTimeout();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
	return session.getLocalAddress();
    }

    @Override
    public WebSocketPolicy getPolicy() {
	return session.getPolicy();
    }

    @Override
    public String getProtocolVersion() {
	return session.getProtocolVersion();
    }

    @Override
    public RemoteEndpoint getRemote() {
	return session.getRemote();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
	return session.getRemoteAddress();
    }

    @Override
    public UpgradeRequest getUpgradeRequest() {
	return session.getUpgradeRequest();
    }

    @Override
    public UpgradeResponse getUpgradeResponse() {
	return session.getUpgradeResponse();
    }

    @Override
    public boolean isOpen() {
	return session.isOpen();
    }

    @Override
    public boolean isSecure() {
	return session.isSecure();
    }

    @Override
    public void setIdleTimeout(final long ms) {
	session.setIdleTimeout(ms);
    }

    @Override
    public SuspendToken suspend() {
	return session.suspend();
    }

    public void send(final String message) {
	try {
	    session.getRemote().sendString(message);
	} catch (final IOException e) {
	    e.printStackTrace();
	}
    }

    public void send(final Object message) {
	send(message.toString());
    }

    public void heartbeat() {
	heartbeats++;
    }

    @SuppressWarnings("unused")
    public static String generateSessionId() {
	final int leftLimit1 = 65; // letter 'A'
	final int rightLimit1 = 90; // letter 'B'
	final int leftLimit2 = 97; // letter 'a'
	final int rightLimit2 = 122; // letter 'z'
	final int leftLimit3 = 48; // 0
	final int rightLimit3 = 57; // 9

	final int targetStringLength = 69;

	final String generatedString = Bot.random.ints(leftLimit3, rightLimit2 + 1).filter(i -> ((i <= rightLimit1 && i >= leftLimit1) || (i <= rightLimit2 && i >= leftLimit2) || (i <= rightLimit3 && i >= leftLimit3))).limit(targetStringLength + 2).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

	// TODO: check if session id is existing
	if (false) return generateSessionId();

	return generatedString;
    }
}