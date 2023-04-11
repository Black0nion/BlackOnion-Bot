package com.github.black0nion.blackonionbot.rest.sessions;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.black0nion.blackonionbot.database.helpers.api.SQLHelperFactory;
import com.github.black0nion.blackonionbot.misc.SQLSetup;
import com.github.black0nion.blackonionbot.misc.exception.OAuthUserNotFoundException;
import com.github.black0nion.blackonionbot.oauth.OAuthUser;
import com.github.black0nion.blackonionbot.oauth.OAuthUserLoader;

import javax.annotation.Nullable;
import java.sql.SQLException;

public abstract sealed class AbstractSession permits RestSession {

	@SQLSetup
	public static void sqlSetup(SQLHelperFactory factory) throws SQLException {
		factory.run("CREATE TABLE IF NOT EXISTS sessions (" +
				"user_id BIGINT NOT NULL PRIMARY KEY," +
				"access_token VARCHAR(255) NOT NULL," +
				"refresh_token VARCHAR(255) NOT NULL," +
				"expires_at BIGINT NOT NULL" +
			")"
		);
	}

	private final DecodedJWT jwt;
	@Nullable
	protected OAuthUser user;

	protected AbstractSession(final DecodedJWT jwt, OAuthUserLoader loadUserFromDb) throws OAuthUserNotFoundException {
		this.jwt = jwt;
		user = loadUserFromDb.apply(Long.valueOf(jwt.getSubject()));
	}

	@Nullable
	public OAuthUser getUser() {
		return this.user;
	}

	public DecodedJWT getJwt() {
		return jwt;
	}
}