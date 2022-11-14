package com.github.black0nion.blackonionbot.rest.sessions;

import com.github.black0nion.blackonionbot.database.SQLHelper;
import com.github.black0nion.blackonionbot.database.SQLHelperFactory;
import com.github.black0nion.blackonionbot.misc.SQLSetup;
import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import com.github.black0nion.blackonionbot.oauth.OAuthHandler;
import com.github.black0nion.blackonionbot.oauth.api.SessionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.concurrent.ExecutionException;

public class DatabaseLogin implements SessionHandler {

	private final SQLHelperFactory sql;

	public DatabaseLogin(SQLHelperFactory sqlHelperFactory) {
		this.sql = sqlHelperFactory;
	}

	private static final Logger logger = LoggerFactory.getLogger(DatabaseLogin.class);

	@SQLSetup
	public static void setup(SQLHelperFactory factory) throws SQLException {
		factory.run("CREATE TABLE IF NOT EXISTS sessions (" +
			SESSION_ID + " VARCHAR(255) PRIMARY KEY, " +
			ACCESS_TOKEN + " VARCHAR(255) NOT NULL, " +
			REFRESH_TOKEN + " VARCHAR(255) NOT NULL, " +
			EXPIRES_AT + " BIGINT" +
		")");
	}

	private static final String SESSION_ID = "session_id";
	public static final String ACCESS_TOKEN = "access_token";
	public static final String REFRESH_TOKEN = "refresh_token";
	public static final String EXPIRES_AT = "expires_at";

	@Override
	public DiscordUser loginToSession(String sessionId) throws ExecutionException, InputMismatchException, NullPointerException, SQLException {
		try (SQLHelper sq = sql.create("SELECT * FROM sessions WHERE " + SESSION_ID + " = ?").addParameter(sessionId);
			 ResultSet rs = sq.executeQuery()) {
			if (rs.next()) {
				return OAuthHandler.getUserWithToken(rs.getString(ACCESS_TOKEN), rs.getString(REFRESH_TOKEN));
			}
		}
		return null;
	}

	@Override
	public void logoutFromSession(String sessionId) throws InputMismatchException, NullPointerException, SQLException {
		try (SQLHelper sq = sql.create("DELETE FROM sessions WHERE " + SESSION_ID + " = ?").addParameter(sessionId);
			 PreparedStatement ps = sq.create()) {
			ps.executeUpdate();
		}
	}

	@Override
	public String createSession(String accessToken, String refreshToken, int expiresIn) {
		try (SQLHelper sq = sql.create("SELECT " + SESSION_ID + " FROM sessions WHERE " + ACCESS_TOKEN + " = ?").addParameter(accessToken);
				ResultSet rs = sq.executeQuery()) {
			if (rs.next()) {
				return rs.getString(SESSION_ID);
			}
		} catch (SQLException e) {
			logger.error("Error while getting existing SessionID", e);
		}

		String sessionId = AbstractSession.generateSessionId();
		try (SQLHelper sq = sql.create("INSERT INTO sessions (session_id, access_token, refresh_token, expires_in) VALUES (? ?, ?, ?)")
					.addParameters(sessionId, accessToken, refreshToken, expiresIn);
				PreparedStatement ps = sq.create()) {
			ps.executeUpdate();
			return sessionId;
		} catch (SQLException e) {
			logger.error("Error while creating session", e);
		}
		throw new IllegalStateException("Could not create session");
	}

	@Override
	public boolean isIdOccupied(String sessionId) {
		try {
			return sql.anyMatch("SELECT " + SESSION_ID + " FROM sessions WHERE session_id = ?", sessionId);
		} catch (SQLException e) {
			logger.error("Could not check if session id is occupied", e);
			return false;
		}
	}
}
