package com.github.black0nion.blackonionbot.api.sessions;

import java.util.InputMismatchException;
import java.util.concurrent.ExecutionException;

public non-sealed class RestSession extends GenericSession {

	public RestSession(String sessionId) throws ExecutionException, InputMismatchException {
		super(sessionId);
	}
}