package com.github.black0nion.blackonionbot.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class SQLHelper implements AutoCloseable {

	private final String rawSQL;
	private final List<Object> parameters = new ArrayList<>();
	private Connection connection;
	private PreparedStatement preparedStatement;
	private final ConnectionSupplier connectionSupplier;

	public SQLHelper(ConnectionSupplier connectionGetter, String rawSQL, Object... parameters) {
		this.connectionSupplier = connectionGetter;
		this.rawSQL = rawSQL;
		if (parameters != null && parameters.length > 0) {
			this.parameters.addAll(Arrays.asList(parameters));
		}
	}

	public SQLHelper(ConnectionSupplier connectionGetter, String rawSQL) {
		this.connectionSupplier = connectionGetter;
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

	public SQLHelper setConnection(Connection connection) {
		this.connection = connection;
		return this;
	}

	public PreparedStatement create() throws SQLException {
		if (connectionSupplier != null && connection == null) connection = connectionSupplier.get();
		if (connection == null) throw new SQLException("No connection acquired!");

		preparedStatement = connection.prepareStatement(rawSQL); // NOSONAR we're returning the PreparedStatement
		if (!parameters.isEmpty()) {
			int index = 1;
			for (Object object : parameters) {
				preparedStatement.setObject(index, object);
				index++;
			}
		}
		return preparedStatement;
	}

	public ResultSet executeQuery() throws SQLException {
		// the connection will be closed by the close() method of this class
		//noinspection resource
		return create().executeQuery();
	}

	public boolean anyMatch() throws SQLException {
		try (ResultSet rs = executeQuery()) {
			return rs.next();
		}
	}

	public boolean execute() throws SQLException {
		try (PreparedStatement ps = create()) {
			return ps.execute();
		}
	}

	/**
	 * Runs the SQL statement <b>and closes the connection</b>.
	 */
	public boolean run() throws SQLException {
		try {
			return execute();
		} finally {
			close();
		}
	}

	public static boolean run(ConnectionSupplier connectionSupplier, String sql, Object... parameters) throws SQLException {
		try (SQLHelper sqlHelper = new SQLHelper(connectionSupplier, sql, parameters)) {
			return sqlHelper.execute();
		}
	}

	@Override
	public void close() throws SQLException {
		connection.close();
		preparedStatement.close();
	}
}
