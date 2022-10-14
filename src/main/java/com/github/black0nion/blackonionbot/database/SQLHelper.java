package com.github.black0nion.blackonionbot.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class SQLHelper {

	private final String rawSQL;
	private final List<Object> parameters = new ArrayList<>();
	private Connection connection = null;
	private boolean closeConnection = false;

	public SQLHelper(String rawSQL, Object... parameters) {
		this.rawSQL = rawSQL;
		this.parameters.addAll(Arrays.asList(parameters));
	}

	public SQLHelper(String rawSQL) {
		this.rawSQL = rawSQL;
	}

	public SQLHelper addParameter(Object object) {
		requireNonNull(object, "object");
		this.parameters.add(object);
		return this;
	}

	public SQLHelper addParameters(Object... objects) {
		requireNonNull(objects, "Objects");
		this.parameters.addAll(Arrays.asList(objects));
		return this;
	}

	public SQLHelper useConnection(Connection connection) {
		this.connection = connection;
		this.closeConnection = false;
		return this;
	}

	public PreparedStatement create() throws SQLException {
		Connection connection = this.connection != null ? this.connection : PostgresConnection.getConnection(); // NOSONAR
		if (connection == null) throw new SQLException("No connection acquired!");

		PreparedStatement ps = connection.prepareStatement(rawSQL); // NOSONAR - we're returning the prepared statement, so we don't need to close it
		if (!parameters.isEmpty()) {
			int index = 1;
			for (Object object : parameters) {
				ps.setObject(index, object);
				index++;
			}
		}
		return ps;
	}

	public boolean execute() throws SQLException {
		Connection connection = this.connection != null ? this.connection : PostgresConnection.getConnection(); // NOSONAR
		if (connection == null) throw new SQLException("Could not acquire connection instance!");

		try (PreparedStatement statement = connection.prepareStatement(rawSQL)) {
			if (!parameters.isEmpty()) {
				int i = 1;
				for (Object object : parameters) {
					statement.setObject(i, object);
					i++;
				}
			}
			return statement.execute();
		} finally {
			if (closeConnection) connection.close();
		}
	}
}
