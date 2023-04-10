package com.github.black0nion.blackonionbot.rest.sessions;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.black0nion.blackonionbot.misc.exception.OAuthUserNotFoundException;
import com.github.black0nion.blackonionbot.oauth.OAuthUserLoader;

/**
 * A wrapper for server-side REST Sessions.
 * Handles authentication through {@link AbstractSession}
 */
public non-sealed class RestSession extends AbstractSession {

	public RestSession(DecodedJWT jwt, OAuthUserLoader loadUserFromDb) throws OAuthUserNotFoundException {
		super(jwt, loadUserFromDb);
	}
}