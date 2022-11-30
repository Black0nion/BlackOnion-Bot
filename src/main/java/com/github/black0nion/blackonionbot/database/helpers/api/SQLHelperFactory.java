package com.github.black0nion.blackonionbot.database.helpers.api;

import com.github.black0nion.blackonionbot.database.SQLHelper;

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
		// the run method already closes everything
		//noinspection resource
		return create(rawSQL, parameters).run();
	}

	/**
	 * Runs the query, returns the value, <b>and closes the connection</b>.
	 *
	 * @return either the first column of the first row of the query or null
	 */
	default String runQuery(String rawSQL) throws SQLException {
		return runQuery(rawSQL, new Object[0]);
	}

	/**
	 * Runs the query and returns the value
	 *
	 * @return either the first column of the first row of the query or null
	 */
	default String runQuery(String rawSQL, Object... parameters) throws SQLException {
		// the runQuery method already closes everything
		//noinspection resource
		return create(rawSQL, parameters).runQuery();
	}
}
