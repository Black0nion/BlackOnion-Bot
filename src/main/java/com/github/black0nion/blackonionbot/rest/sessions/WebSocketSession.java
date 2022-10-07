package com.github.black0nion.blackonionbot.rest.sessions;

import org.eclipse.jetty.websocket.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.time.Duration;
import java.util.InputMismatchException;

/**
 * A wrapper for server-side WebSocket Sessions.
 * Handles things like logins to the dashboard, heartbeats and possibly more.
 */
public non-sealed class WebSocketSession extends AbstractSession implements Session {

	public WebSocketSession(final Session sessionRaw) throws InputMismatchException {
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
		logger.info("IP {} will get closed with code {} and reason {}", this.getIp(), statusCode, reason);
		this.session.close(statusCode, reason);
	}

	@Override
	public void disconnect() {
		logger.info("IP {} disconnected", this.getIp());
		this.session.disconnect();
	}

	@Override
	public SocketAddress getLocalAddress() {
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
	public SocketAddress getRemoteAddress() {
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
		return this.session.getRemoteAddress().toString();
	}

	@Override
	public void close(int statusCode, String reason, WriteCallback callback) {
		this.session.close(statusCode, reason, callback);
	}

	@Override
	public WebSocketBehavior getBehavior() {
		return this.session.getBehavior();
	}

	@Override
	public Duration getIdleTimeout() {
		return null;
	}

	@Override
	public int getInputBufferSize() {
		return this.session.getInputBufferSize();
	}

	@Override
	public int getOutputBufferSize() {
		return this.session.getOutputBufferSize();
	}

	@Override
	public long getMaxBinaryMessageSize() {
		return this.session.getMaxBinaryMessageSize();
	}

	@Override
	public long getMaxTextMessageSize() {
		return this.session.getMaxTextMessageSize();
	}

	@Override
	public long getMaxFrameSize() {
		return this.session.getMaxFrameSize();
	}

	@Override
	public boolean isAutoFragment() {
		return this.session.isAutoFragment();
	}

	@Override
	public void setIdleTimeout(Duration duration) {
		this.session.setIdleTimeout(duration);
	}

	@Override
	public void setInputBufferSize(int size) {
		this.session.setInputBufferSize(size);
	}

	@Override
	public void setOutputBufferSize(int size) {
		this.session.setOutputBufferSize(size);
	}

	@Override
	public void setMaxBinaryMessageSize(long size) {
		this.session.setMaxBinaryMessageSize(size);
	}

	@Override
	public void setMaxTextMessageSize(long size) {
		this.session.setMaxTextMessageSize(size);
	}

	@Override
	public void setMaxFrameSize(long maxFrameSize) {
		this.session.setMaxFrameSize(maxFrameSize);
	}

	@Override
	public void setAutoFragment(boolean autoFragment) {
		this.session.setAutoFragment(autoFragment);
	}
}
