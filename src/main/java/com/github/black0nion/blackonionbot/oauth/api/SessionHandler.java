package com.github.black0nion.blackonionbot.oauth.api;

import com.github.black0nion.blackonionbot.oauth.DiscordUser;

import java.util.InputMismatchException;
import java.util.concurrent.ExecutionException;

public interface SessionHandler {
	String SESSIONID = "sessionid";

	DiscordUser loginToSession(String sessionId) throws ExecutionException, InputMismatchException, NullPointerException;
	void logoutFromSession(String sessionId) throws InputMismatchException, NullPointerException;

	/**
	 * @return the session id of the newly generated session
	 */
	String createSession(String accessToken, String refreshToken, int expiresIn);

	default boolean isIdOccupied(String sessionId) {
		return false;
	}
}
