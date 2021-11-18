package com.github.black0nion.blackonionbot.API;

public abstract class WebSocketEndpoint {
    private String route;

    public String getRoute() {
	return this.route;
    }

    public void setRoute(final String route) {
	this.route = route;
    }
}