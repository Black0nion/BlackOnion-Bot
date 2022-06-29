package com.github.black0nion.blackonionbot.api.sessions;

import java.util.InputMismatchException;
import java.util.concurrent.ExecutionException;

/**
 * A wrapper for server-side REST Sessions.
 * Handles authentication through {@link AbstractSession}
 */
public non-sealed class RestSession extends AbstractSession {

	public RestSession(String sessionId) throws ExecutionException, InputMismatchException {
		super(sessionId);
	}
}