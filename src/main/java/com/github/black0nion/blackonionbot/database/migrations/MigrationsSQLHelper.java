package com.github.black0nion.blackonionbot.database.migrations;

import com.github.black0nion.blackonionbot.database.ConnectionSupplier;
import com.github.black0nion.blackonionbot.database.SQLHelper;

import java.sql.SQLException;

public class MigrationsSQLHelper extends SQLHelper {

	public MigrationsSQLHelper(boolean logConnectionReleases, ConnectionSupplier connectionGetter, String rawSQL, Object... parameters) {
		super(logConnectionReleases, connectionGetter, rawSQL, parameters);
	}

	@Override
	public void close() throws SQLException {
		// do not close the connection
		if (preparedStatement != null && !preparedStatement.isClosed()) preparedStatement.close();
	}
}
