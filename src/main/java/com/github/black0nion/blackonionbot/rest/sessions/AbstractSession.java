package com.github.black0nion.blackonionbot.rest.sessions;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.black0nion.blackonionbot.oauth.OAuthUser;

import javax.annotation.Nullable;
import java.util.InputMismatchException;

public abstract sealed class AbstractSession permits RestSession {

	private final DecodedJWT jwt;
	@Nullable
	protected OAuthUser user;

	protected AbstractSession(final DecodedJWT jwt) throws InputMismatchException {
		this.jwt = jwt;
	}

	@Nullable
	public OAuthUser getUser() {
		return this.user;
	}
}