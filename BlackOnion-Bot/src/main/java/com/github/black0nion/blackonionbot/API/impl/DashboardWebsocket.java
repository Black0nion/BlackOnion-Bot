package com.github.black0nion.blackonionbot.API.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.github.black0nion.blackonionbot.API.WebSocketEndpoint;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.systems.dashboard.BlackSession;
import com.github.black0nion.blackonionbot.systems.dashboard.Dashboard;
import com.github.black0nion.blackonionbot.systems.dashboard.DiscordLogin;
import com.github.black0nion.blackonionbot.systems.logging.Logger;
import com.github.black0nion.blackonionbot.utils.ValueManager;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@WebSocket
public class DashboardWebsocket extends WebSocketEndpoint {

    public DashboardWebsocket() {
	this.setRoute("dashboard");
    }

    private static boolean logHeartbeats = ValueManager.getBoolean("logHeartbeats");

    public static final List<String> requiredScopes = Arrays.asList("guilds identify");

    private static final List<Session> sessions = new ArrayList<>();

    private static final LoadingCache<Session, BlackSession> blacksessions = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build(new CacheLoader<Session, BlackSession>() {
	@Override
	public BlackSession load(final Session key) throws Exception {
	    return new BlackSession(key);
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
	final BlackSession session = blacksessions.getUnchecked(sessionUnchecked);
	if (message.equals("heartbeat")) {
	    futures.get(session).cancel(true);
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
	    // login|<code> = login with session id
	    // login><code> = login with discord, generate session id
	    final String[] argsNormal = message.split("\\|");
	    final String[] argsDiscord = message.split(">");
	    if (argsNormal.length == 2) {
		// the user already created a session id once
		final boolean success = session.loginToSession(argsNormal[1]);
		session.send(success);
	    } else if (argsDiscord.length == 2) {
		// the user needs a new session
		final DiscordLogin success = session.loginWithDiscord(argsDiscord[1]);
		if (success.success()) {
		    session.send("id|" + success.success());
		} else {
		    final DiscordLogin.SessionError error = success.getError();
		    session.send("err|" + error.getCode() + (error.getDescription() != null ? "|" + error.getDescription() : ""));
		}
	    } else {
		// failure
		session.send("nah bro");
	    }
	}

	Logger.logInfo("IP " + session.getRemote().getInetSocketAddress().getAddress().getHostAddress() + " Received: " + message.replace("\n", "\\n"), LogOrigin.DASHBOARD);
    }
}