package com.github.black0nion.blackonionbot.API;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;

public interface WebSocketEndpoint {
	String getRoute();
	
	default void send(Session session, String message) {
		try {
			session.getRemote().sendString(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}