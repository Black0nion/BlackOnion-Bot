package com.github.black0nion.blackonionbot.API;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;

public abstract class WebSocketEndpoint {
    private String route;

    public String getRoute() {
	return this.route;
    }

    public void setRoute(final String route) {
	this.route = route;
    }

    public void send(final Session session, final String message) {
	try {
	    session.getRemote().sendString(message);
	} catch (final IOException e) {
	    e.printStackTrace();
	}
    }
}