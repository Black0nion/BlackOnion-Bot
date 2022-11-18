package com.github.black0nion.blackonionbot.database;

import com.github.black0nion.blackonionbot.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class SQLHelper implements AutoCloseable {

	private static final Logger logger = LoggerFactory.getLogger(SQLHelper.class);
	private final String rawSQL;
	private final List<Object> parameters = new ArrayList<>();
	protected Connection connection;
	protected PreparedStatement preparedStatement;
	private final boolean logConnectionReleases;
	private final ConnectionSupplier connectionSupplier;

	public SQLHelper(boolean logConnectionReleases, ConnectionSupplier connectionGetter, String rawSQL, Object... parameters) {
		this.logConnectionReleases = logConnectionReleases;
		this.connectionSupplier = connectionGetter;
		this.rawSQL = rawSQL;
		if (parameters != null && parameters.length > 0) {
			this.parameters.addAll(Arrays.asList(parameters));
		}
	}

	public SQLHelper(boolean logConnectionReleases, ConnectionSupplier connectionGetter, String rawSQL) {
		this.logConnectionReleases = logConnectionReleases;
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
		} finally {
			close();
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

	/**
	 * Runs the query, returns the value, <b>and closes the connection</b>.
	 *
	 * @return either the first column of the first row of the query or null
	 */
	public String runQuery() throws SQLException {
		try (ResultSet rs = executeQuery()) {
			if (rs.next()) {
				return rs.getString(1);
			}
		} finally {
			this.close();
		}
		return null;
	}

	@Override
	public void close() throws SQLException {
		connection.close();
		preparedStatement.close();
		if (logConnectionReleases && logger.isDebugEnabled()) {
			logger.debug("Closed connection requested from: {}",
				Utils.stackTraceToString(Arrays.copyOfRange(Thread.currentThread().getStackTrace(), 2, 10)));
		}
	}
}
