package com.github.black0nion.blackonionbot.api;

import org.eclipse.jetty.websocket.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.InputMismatchException;
import java.util.concurrent.ExecutionException;

public class BlackWebsocketSession extends BlackSession implements org.eclipse.jetty.websocket.api.Session {

	public BlackWebsocketSession(final Session sessionRaw) throws InputMismatchException, ExecutionException {
		super(sessionRaw.getUpgradeRequest().getHeader("Sec-WebSocket-Protocol"));
		this.session = sessionRaw;
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

	private static final Logger logger = LoggerFactory.getLogger("Websocket Session");

	@Override
	public void close(final int statusCode, final String reason) {
		logger.info("IP {} will get closed with code {} and reason {}", this.session.getRemote().getInetSocketAddress().getAddress().getHostAddress(), statusCode, reason);
		this.session.close(statusCode, reason);
	}

	@Override
	public void disconnect() throws IOException {
		logger.info("IP {} disconnected", this.session.getRemote().getInetSocketAddress().getAddress().getHostAddress());
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

	public void heartbeat(final String id) {
		this.heartbeat();
		this.send("heartbeat" + id);
	}

	public String getIp() {
		return this.session.getRemote().getInetSocketAddress().getAddress().getHostAddress();
	}
}