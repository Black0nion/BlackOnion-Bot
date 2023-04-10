package com.github.black0nion.blackonionbot.rest.sessions;

import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.InputMismatchException;

/**
 * A wrapper for server-side REST Sessions.
 * Handles authentication through {@link AbstractSession}
 */
public non-sealed class RestSession extends AbstractSession {

	public RestSession(DecodedJWT jwt) throws InputMismatchException {
		super(jwt);
	}
}