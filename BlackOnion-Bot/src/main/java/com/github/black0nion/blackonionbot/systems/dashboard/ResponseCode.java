/**
 *
 */
package com.github.black0nion.blackonionbot.systems.dashboard;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.API.BlackWebsocketSession;
import com.github.black0nion.blackonionbot.API.impl.DashboardWebsocket;

/**
 * @author _SIM_
 *
 */
public enum ResponseCode {

    SUCCESS(true), FAIL("bruh"), NO_ACTION("No action specified."), WRONG_ARGUMENTS, UNAUTHORIZED, JSON_ERROR("Invalid JSON Syntax."), NO_GUILD("No guild specified."), INVALID_TYPE("This type doesn't exist."), WRONG_SETTING("This setting doesn't exist."), NO_PERMISSIONS("You are not allowed to do that."), PARSE_ERROR("A argument has the wrong type."), LOGGED_IN(true, "You got logged in.");

    private final JSONObject json;

    /**
     * Success is false
     */
    private ResponseCode() {
	this(false);
    }

    /**
     * Success is false
     *
     * @param message
     */
    private ResponseCode(final String message) {
	this(false, message);
    }

    private ResponseCode(final boolean success, final String message) {
	this.json = new JSONObject().put("success", success).put("code", this.name()).put("message", message);
    }

    private ResponseCode(final boolean success) {
	this.json = new JSONObject().put("success", success).put("code", this.name());
    }

    private ResponseCode(final JSONObject json) {
	this.json = json;
    }

    /**
     * @return the json
     */
    public JSONObject getJson() {
	return this.json;
    }

    public void send(final BlackWebsocketSession session, final JSONObject request) {
	DashboardWebsocket.reply(session, request, this.getJson());
    }
}