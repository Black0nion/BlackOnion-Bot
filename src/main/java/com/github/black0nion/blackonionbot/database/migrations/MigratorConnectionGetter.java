package com.github.black0nion.blackonionbot.database.migrations;

import com.github.black0nion.blackonionbot.database.ConnectionSupplier;
import com.github.black0nion.blackonionbot.database.DatabaseConnector;

import java.sql.Connection;
import java.sql.SQLException;

public class MigratorConnectionGetter {

	private final ConnectionSupplier supplier;

	public MigratorConnectionGetter(DatabaseConnector connector) throws SQLException {
		//noinspection resource
		Connection connection = connector.retrieveConnection(false);
		connection.setAutoCommit(false);
		this.supplier = () -> connection;
	}

	public ConnectionSupplier getSupplier() {
		return supplier;
	}
}
