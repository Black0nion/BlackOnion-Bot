package com.github.black0nion.blackonionbot.database;

import java.sql.SQLException;

public interface SQLHelperFactory {

	default SQLHelper create(String rawSQL) throws SQLException {
		return create(rawSQL, new Object[0]);
	}

	SQLHelper create(String rawSQL, Object... parameters) throws SQLException;

	default boolean anyMatch(String rawSQL) throws SQLException {
		return anyMatch(rawSQL, new Object[0]);
	}

	default boolean anyMatch(String rawSQL, Object... parameters) throws SQLException {
		try (SQLHelper sqlHelper = create(rawSQL, parameters)) {
			return sqlHelper.anyMatch();
		}
	}

	default boolean run(String rawSQL) throws SQLException {
		return run(rawSQL, new Object[0]);
	}

	default boolean run(String rawSQL, Object... parameters) throws SQLException {
		//noinspection resource
		return create(rawSQL, parameters).run();
	}
}
