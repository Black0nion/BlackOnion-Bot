/**
 *
 */
package com.github.black0nion.blackonionbot.API;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.eclipse.jetty.websocket.api.CloseStatus;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.SuspendToken;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;

import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.systems.logging.Logger;

/**
 * @author _SIM_
 *
 */
public class BlackWebsocketSession extends BlackSession implements org.eclipse.jetty.websocket.api.Session {

    public BlackWebsocketSession(final Session session) {
	this.session = session;
	this.createSession();
    }

    private final Session session;
    @SuppressWarnings("unused")
    private int heartbeats;

    @Override
    public void close() {
	this.session.close();
    }

    @Override
    public void close(final CloseStatus closeStatus) {
	this.session.close(closeStatus);
    }

    @Override
    public void close(final int statusCode, final String reason) {
	Logger.logInfo("IP " + this.session.getRemote().getInetSocketAddress().getAddress().getHostAddress() + " got yeeted out.", LogOrigin.DASHBOARD);
	this.session.close(statusCode, reason);
    }

    @Override
    public void disconnect() throws IOException {
	Logger.logInfo("IP " + this.session.getRemote().getInetSocketAddress().getAddress().getHostAddress() + " got yeeted out.", LogOrigin.DASHBOARD);
	this.session.disconnect();
    }

    @Override
    public long getIdleTimeout() {
	return this.session.getIdleTimeout();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
	return this.session.getLocalAddress();
    }

    @Override
    public WebSocketPolicy getPolicy() {
	return this.session.getPolicy();
    }

    @Override
    public String getProtocolVersion() {
	return this.session.getProtocolVersion();
    }

    @Override
    public RemoteEndpoint getRemote() {
	return this.session.getRemote();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
	return this.session.getRemoteAddress();
    }

    @Override
    public UpgradeRequest getUpgradeRequest() {
	return this.session.getUpgradeRequest();
    }

    @Override
    public UpgradeResponse getUpgradeResponse() {
	return this.session.getUpgradeResponse();
    }

    @Override
    public boolean isOpen() {
	return this.session.isOpen();
    }

    @Override
    public boolean isSecure() {
	return this.session.isSecure();
    }

    @Override
    public void setIdleTimeout(final long ms) {
	this.session.setIdleTimeout(ms);
    }

    @Override
    public SuspendToken suspend() {
	return this.session.suspend();
    }

    public void send(final String message) {
	try {
	    this.session.getRemote().sendString(message);
	} catch (final IOException e) {
	    e.printStackTrace();
	}
    }

    public void send(final Object message) {
	this.send(message.toString());
    }

    public void heartbeat() {
	this.heartbeats++;
    }
}