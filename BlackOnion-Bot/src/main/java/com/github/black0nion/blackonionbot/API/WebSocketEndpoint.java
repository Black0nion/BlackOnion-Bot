package com.github.black0nion.blackonionbot.API;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;

public abstract class WebSocketEndpoint {
	
	String route;
	
	public String getRoute() {
		return this.route;
	}
	
	public void setRoute(String route) {
		this.route = route;
	}
	
	public void send(Session session, String message) {
		try {
			session.getRemote().sendString(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}