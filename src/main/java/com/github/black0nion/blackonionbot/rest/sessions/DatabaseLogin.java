package com.github.black0nion.blackonionbot.rest.sessions;

import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import com.github.black0nion.blackonionbot.oauth.OAuthHandler;
import com.github.black0nion.blackonionbot.oauth.api.SessionHandler;

import java.util.InputMismatchException;
import java.util.concurrent.ExecutionException;

// TODO: implement
public class DatabaseLogin implements SessionHandler {

	public static final String ACCESS_TOKEN = "access_token";
	public static final String REFRESH_TOKEN = "refresh_token";

	@Override
	public DiscordUser loginToSession(String sessionId) throws ExecutionException, InputMismatchException, NullPointerException {
		if (true)
			return null;
		return OAuthHandler.getUserWithToken(ACCESS_TOKEN, REFRESH_TOKEN);
	}

	@Override
	public void logoutFromSession(String sessionId) throws InputMismatchException, NullPointerException {
		// TODO: delete session
	}

	@Override
	public String createSession(String accessToken, String refreshToken, int expiresIn) {
		/*final Document find = getCollection().find(Filters.and(Filters.eq(ACCESS_TOKEN, accessToken), Filters.eq(REFRESH_TOKEN, refreshToken), Filters.exists("sessionid"))).first();
		if (find != null) return find.getString(SESSIONID);
		final String newSessionId = AbstractSession.generateSessionId();
		getCollection().insertOne(new Document()
				.append(SESSIONID, newSessionId)
				.append(ACCESS_TOKEN, accessToken)
				.append(REFRESH_TOKEN, refreshToken)
				.append("expires_in", expiresIn));
		*/
		return null;
	}

	@Override
	public boolean isIdOccupied(String sessionId) {
		return false;
	}
}
