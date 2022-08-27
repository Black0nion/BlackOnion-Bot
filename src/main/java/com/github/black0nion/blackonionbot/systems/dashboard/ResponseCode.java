package com.github.black0nion.blackonionbot.systems.dashboard;

import com.github.black0nion.blackonionbot.rest.sessions.WebSocketSession;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.rest.impl.DashboardWebsocket;

public enum ResponseCode {

	SUCCESS(true),
	FAIL("epic fail bru bru"),
	NO_ACTION("No action specified."),
	WRONG_ARGUMENTS, UNAUTHORIZED,
	JSON_ERROR("Invalid JSON Syntax."),
	NO_GUILD("No guild specified."),
	INVALID_TYPE("This type doesn't exist."),
	WRONG_SETTING("This setting doesn't exist."),
	NO_PERMISSIONS("You are not allowed to do that."),
	PARSE_ERROR("A argument has the wrong type."),
	LOGGED_IN(true, "You got logged in.");

	private final JSONObject json;

	/**
	 * Success is false
	 */
	ResponseCode() {
		this(false);
	}

	/**
	 * Success is false
	 */
	ResponseCode(final String message) {
		this(false, message);
	}

	ResponseCode(final boolean success, final String message) {
		this.json = new JSONObject().put("code", (success ? "S_" : "E_") + this.name()).put("message", message);
	}

	ResponseCode(final boolean success) {
		this.json = new JSONObject().put("code", (success ? "S_" : "E_") + this.name());
	}

	/**
	 * @return the json
	 */
	public JSONObject getJson() {
		return this.json;
	}

	public void send(final WebSocketSession session) {
		send(session, null);
	}

	public void send(final WebSocketSession session, final JSONObject request) {
		DashboardWebsocket.reply(session, request, this.getJson());
	}
}
