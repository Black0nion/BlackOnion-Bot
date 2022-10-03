package com.github.black0nion.blackonionbot.rest.api;

import org.eclipse.jetty.websocket.api.Session;

public interface IWebSocketEndpoint extends IRoute {

    void onConnect(Session session);

    void onMessage(Session session, String message);

    void onClose(Session session, int statusCode, String reason);

    void onError(Session session, Throwable throwable);
}
