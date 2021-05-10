package com.github.black0nion.blackonionbot.API.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.CloseStatus;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.github.black0nion.blackonionbot.Logger;
import com.github.black0nion.blackonionbot.API.WebSocketEndpoint;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.misc.LogOrigin;

@WebSocket
public class UpdateValue implements WebSocketEndpoint {
	private static final List<Session> sessions = new ArrayList<>();
	
	private static final HashMap<Session, ScheduledFuture<?>> futures = new HashMap<>();
	
	@OnWebSocketConnect
	public void connected(Session session) {
		sessions.add(session);
		Logger.logInfo("IP " + session.getRemote().getInetSocketAddress().getAddress().getHostAddress() + " Connected to Dashboard Websocket.", LogOrigin.DASHBOARD);
		futures.put(session, Bot.scheduledExecutor.schedule(() -> {
			Logger.logInfo("IP " + session.getRemote().getInetSocketAddress().getAddress().getHostAddress() + " Timed Out.", LogOrigin.DASHBOARD);
			session.close(4408, "Missed Heartbeat");
		}, 1, TimeUnit.SECONDS));
	}
	
	@OnWebSocketClose
	public void closed(Session session, int statusCode, String reason) {
		Logger.logInfo("IP " + session.getRemote().getInetSocketAddress().getAddress().getHostAddress() + " Disconnected.", LogOrigin.DASHBOARD);
		sessions.remove(session);
	}
	
	@OnWebSocketMessage
	public void message(Session session, String message) {
		Logger.logInfo("IP " + session.getRemote().getInetSocketAddress().getAddress().getHostAddress() + " Received: " + message.replace("\n", "\\n"), LogOrigin.DASHBOARD);
		if (message.equals("heartbeat")) {
			Logger.logInfo("IP " + session.getRemote().getInetSocketAddress().getAddress().getHostAddress() + " Heartbeat.", LogOrigin.DASHBOARD);
			futures.get(session).cancel(true);
			futures.put(session, Bot.scheduledExecutor.schedule(() -> {
				Logger.logInfo("IP " + session.getRemote().getInetSocketAddress().getAddress().getHostAddress() + " Timed Out.", LogOrigin.DASHBOARD);
				session.close(4408, "Missed Heartbeat");
			}, 1, TimeUnit.SECONDS));
		}
		try {
			session.getRemote().sendString("Hi :)");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getRoute() {
		return "echo";
	}
}