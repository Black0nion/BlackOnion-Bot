package com.github.black0nion.blackonionbot.database;

import com.github.black0nion.blackonionbot.Main;
import com.github.black0nion.blackonionbot.config.immutable.api.Config;
import com.github.black0nion.blackonionbot.misc.SQLSetup;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class PostgresConnection {

	private static final Logger logger = LoggerFactory.getLogger(PostgresConnection.class);

	private final HikariDataSource ds;

	private static PostgresConnection instance;
	public static PostgresConnection getInstance() {
		return instance;
	}

	public PostgresConnection(Config config) {
		if (instance != null) instance.close();
		instance = this; // NOSONAR
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setJdbcUrl(config.getJdbcUrl());
		hikariConfig.setUsername(config.getPostgresUsername());
		hikariConfig.setPassword(config.getPostgresPassword());

		ds = new HikariDataSource(hikariConfig);

		// find all methods annotated with @SQLSetup and run them
		new Reflections(Main.class.getPackage().getName(), Scanners.MethodsAnnotated)
			.getMethodsAnnotatedWith(SQLSetup.class)
			.stream()
			// we don't care at what point the peek will be executed so the SonarLint warning can be ignored
			// also, in this case, I'm accessing my own code with reflections, so I know what I'm doing (I think)
			.peek(m -> m.setAccessible(true)) // NOSONAR
			.forEach(method -> Utils.uncheckedSupplier(() -> method.invoke(null)));
	}

	public Connection acquireConnection() throws SQLException {
		return ds.getConnection();
	}

	public static Connection getConnection() throws SQLException {
		return instance.acquireConnection();
	}

	public static Connection getSilentConnection() {
		try {
			return getInstance().ds.getConnection();
		} catch (SQLException e) {
			logger.error("Error while getting connection", e);
			return null;
		}
	}

	public void close() {
		ds.close();
	}
}
