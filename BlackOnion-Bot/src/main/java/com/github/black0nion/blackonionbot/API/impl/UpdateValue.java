package com.github.black0nion.blackonionbot.API.impl;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.github.black0nion.blackonionbot.Logger;
import com.github.black0nion.blackonionbot.API.WebSocketEndpoint;
import com.github.black0nion.blackonionbot.misc.LogOrigin;

@WebSocket
public class UpdateValue implements WebSocketEndpoint {
	private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();
	
	@OnWebSocketConnect
	public void connected(Session session) {
		sessions.add(session);
		Logger.logInfo("IP " + session.getRemote().getInetSocketAddress().getAddress().getHostAddress() + " Connected to Dashboard Websocket.", LogOrigin.DASHBOARD);
		while (true) {
			try {
				Thread.sleep(2000);
				System.out.println("none lmao");
				sessions.forEach(ses -> { System.out.println(ses.getRemoteAddress().getHostName()); });
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@OnWebSocketClose
	public void closed(Session session, int statusCode, String reason) {
		Logger.logInfo("IP " + session.getRemote().getInetSocketAddress().getAddress().getHostAddress() + " Disconnected.", LogOrigin.DASHBOARD);
		sessions.remove();
	}
	
	@OnWebSocketMessage
	public void message(Session session, String message) {
		Logger.logInfo("IP " + session.getRemote().getInetSocketAddress().getAddress().getHostAddress() + " Received: " + message.replace("\n", "\\n"), LogOrigin.DASHBOARD);
		try {
			session.getRemote().sendString(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getRoute() {
		return "echo";
	}
}
