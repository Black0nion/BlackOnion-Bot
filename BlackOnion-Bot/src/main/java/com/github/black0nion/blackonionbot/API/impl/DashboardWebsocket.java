package com.github.black0nion.blackonionbot.API.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.bson.Document;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.github.black0nion.blackonionbot.API.BlackSession;
import com.github.black0nion.blackonionbot.API.BlackWebsocketSession;
import com.github.black0nion.blackonionbot.API.WebSocketEndpoint;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.systems.dashboard.Dashboard;
import com.github.black0nion.blackonionbot.systems.logging.Logger;
import com.github.black0nion.blackonionbot.utils.ValueManager;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mongodb.client.model.Filters;

@WebSocket
public class DashboardWebsocket extends WebSocketEndpoint {

    public DashboardWebsocket() {
	this.setRoute("dashboard");
    }

    private static boolean logHeartbeats = ValueManager.getBoolean("logHeartbeats");

    public static final List<String> requiredScopes = Arrays.asList("guilds", "identify");

    private static final List<Session> sessions = new ArrayList<>();

    private static final LoadingCache<Session, BlackWebsocketSession> BlackWebsocketSessions = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build(new CacheLoader<Session, BlackWebsocketSession>() {
	@Override
	public BlackWebsocketSession load(final Session key) throws Exception {
	    return new BlackWebsocketSession(key);
	};
    });

    private static final HashMap<Session, ScheduledFuture<?>> futures = new HashMap<>();

    @OnWebSocketConnect
    public void connected(final Session session) {
	sessions.add(session);
	Logger.logInfo("IP " + session.getRemote().getInetSocketAddress().getAddress().getHostAddress() + " Connected to Dashboard Websocket.", LogOrigin.DASHBOARD);
	futures.put(session, Bot.scheduledExecutor.schedule(() -> {
	    Logger.logInfo("IP " + session.getRemote().getInetSocketAddress().getAddress().getHostAddress() + " Timed Out.", LogOrigin.DASHBOARD);
	    session.close(4408, "Mach dich aus meiner Leitung raus, du Birne!");
	}, 1, TimeUnit.MINUTES));
    }

    @OnWebSocketClose
    public void closed(final Session session, final int statusCode, final String reason) {
	sessions.remove(session);
    }

    @OnWebSocketMessage
    public void message(final Session sessionUnchecked, final String message) {
	// TODO: error handling
	final BlackWebsocketSession session = BlackWebsocketSessions.getUnchecked(sessionUnchecked);
	if (message.equals("heartbeat")) {
	    futures.get(sessionUnchecked).cancel(true);
	    if (logHeartbeats) {
		Logger.logInfo("IP " + session.getRemote().getInetSocketAddress().getAddress().getHostAddress() + " Heartbeat.", LogOrigin.DASHBOARD);
	    }
	    session.send("heartbeat");
	    futures.put(session, Bot.scheduledExecutor.schedule(() -> {
		Logger.logInfo("IP " + session.getRemote().getInetSocketAddress().getAddress().getHostAddress() + " Timed Out.", LogOrigin.DASHBOARD);
		session.close(4408, "Mach dich aus meiner Leitung raus, du Birne!");
	    }, 1, TimeUnit.MINUTES));
	    return;
	} else if (message.startsWith("updatevalue")) {
	    if (Dashboard.tryUpdateValue(message)) {
		session.send("success");
	    } else {
		session.send("failure");
	    }
	} else if (message.startsWith("login")) {
	    // TODO: implement login
	    // TODO: check if that todo isn't already done
	    final Document doc = BlackSession.collection.find(Filters.eq("sessionid", message.replace("login", ""))).first();
	    if (doc == null) {
		session.close(4401, "Unauthorized");
	    } else {
		session.loginToSession(doc.getString("access_token"));
		session.send("worked yay");
	    }
	} else if (message.startsWith("userinfo")) {
	    System.out.println(session);
	    session.send(session.getUser());
	} else if (message.startsWith("guildsettings")) {
	    // TODO: implement
	}

	Logger.logInfo("IP " + session.getRemote().getInetSocketAddress().getAddress().getHostAddress() + " Received: " + message.replace("\n", "\\n"), LogOrigin.DASHBOARD);
    }
}