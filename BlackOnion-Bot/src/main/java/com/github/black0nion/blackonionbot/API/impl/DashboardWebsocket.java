package com.github.black0nion.blackonionbot.API.impl;

import java.io.IOException;
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
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.API.BlackSession;
import com.github.black0nion.blackonionbot.API.BlackWebsocketSession;
import com.github.black0nion.blackonionbot.API.WebSocketEndpoint;
import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.systems.dashboard.Dashboard;
import com.github.black0nion.blackonionbot.systems.logging.Logger;
import com.github.black0nion.blackonionbot.utils.ValueManager;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mongodb.client.model.Filters;

import net.dv8tion.jda.api.Permission;

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
    public void connected(final Session sessionRaw) {
	try {
	    final String sessionId = sessionRaw.getUpgradeRequest().getHeader("Sec-WebSocket-Protocol");
	    if (sessionId == null) {
		sessionRaw.close(4401, "Unauthorized");
		return;
	    }
	    final Document doc = BlackSession.collection.find(Filters.eq("sessionid", sessionId)).first();
	    if (doc == null) {
		sessionRaw.close(4401, "Unauthorized");
		return;
	    }
	    final BlackWebsocketSession session = BlackWebsocketSessions.get(sessionRaw);
	    session.send("worked yay");
	    sessions.add(session);
	    Logger.logInfo("IP " + session.getRemote().getInetSocketAddress().getAddress().getHostAddress() + " Connected to Dashboard Websocket.", LogOrigin.DASHBOARD);
	    futures.put(session, this.scheduleTimeout(session));
	} catch (final Exception e) {
	    e.printStackTrace();
	}
    }

    @OnWebSocketClose
    public void closed(final Session session, final int statusCode, final String reason) {
	sessions.remove(session);
    }

    @OnWebSocketMessage
    public void message(final Session sessionUnchecked, final String messageRaw) {
	final BlackWebsocketSession session = BlackWebsocketSessions.getUnchecked(sessionUnchecked);
	Logger.logInfo("IP " + session.getRemote().getInetSocketAddress().getAddress().getHostAddress() + " Received: " + messageRaw.replace("\n", "\\n"), LogOrigin.DASHBOARD);
	if (messageRaw.charAt(0) == 'r') {
	    try {
		final JSONObject message = new JSONObject(messageRaw.substring(1));
		if (!message.has("action")) {
		    session.send("nope");
		    return;
		}
		final String command = message.getString("action");
		if (command.equalsIgnoreCase("updatesetting")) {
		    if (Dashboard.tryUpdateValue(message, session.getUser())) {
			session.send("success");
		    } else {
			session.send("failure");
		    }
		} else if (command.equalsIgnoreCase("userinfo")) {
		    session.send(session.getUser());
		} else if (command.equalsIgnoreCase("guildsettings")) {
		    if (!(message.has("guildid") && message.has("settings"))) {
			session.send("wrong arguments");
			return;
		    }
		    final Object guildid = message.get("guildid");
		    BlackGuild guild;
		    if (guildid instanceof String) {
			guild = BlackGuild.from(Long.parseLong((String) guildid));
		    } else if (guildid instanceof Long) {
			guild = BlackGuild.from((Long) guildid);
		    } else {
			session.send("No guild specified.");
			return;
		    }
		    guild.retrieveMemberById(session.getUser().getUserId()).queue(member -> {
			if (!member.hasPermission(Permission.MESSAGE_MANAGE)) {
			    session.send("Unauthorized.");
			    return;
			}
			final JSONObject response = new JSONObject();
			final JSONArray settings = message.getJSONArray("settings");
			for (int i = 0; i < settings.length(); i++) {
			    try {
				final String arg = settings.getString(i);
				if (!Dashboard.getters.containsKey(arg)) {
				    response.put(arg, "null");
				} else {
				    response.put(arg, Dashboard.getters.get(arg).invoke(guild));
				}
			    } catch (final Exception e) {
				e.printStackTrace();
			    }
			}
			session.send(response.toString());
		    });
		}
	    } catch (final Exception e) {
		if (!(e instanceof JSONException)) {
		    e.printStackTrace();
		} else {
		    session.send("JSON Error");
		}
	    }
	} else if (messageRaw.charAt(0) == 'n') {
	    if (messageRaw.equals("heartbeat")) {
		futures.get(sessionUnchecked).cancel(true);
		if (logHeartbeats) {
		    Logger.logInfo("IP " + session.getRemote().getInetSocketAddress().getAddress().getHostAddress() + " Heartbeat.", LogOrigin.DASHBOARD);
		}
		session.send("heartbeat");
		futures.put(session, this.scheduleTimeout(session));
		return;
	    }
	} else {
	    session.send("invalid type.");
	}
    }

    @OnWebSocketError
    public void onError(final Session session, final Throwable error) {
	error.printStackTrace();
	try {
	    session.getRemote().sendString("Some error happened :/");
	} catch (final IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * @param  session
     * @return
     */
    private ScheduledFuture<?> scheduleTimeout(final BlackWebsocketSession session) {
	return Bot.scheduledExecutor.schedule(() -> {
	    Logger.logInfo("IP " + session.getRemote().getInetSocketAddress().getAddress().getHostAddress() + " Timed Out.", LogOrigin.DASHBOARD);
	    session.close(4408, "Mach dich aus meiner Leitung raus, du Birne!");
	}, 1, TimeUnit.MINUTES);
    }
}