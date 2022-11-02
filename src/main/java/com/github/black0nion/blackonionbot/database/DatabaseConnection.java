package com.github.black0nion.blackonionbot.database;

import com.github.black0nion.blackonionbot.Main;
import com.github.black0nion.blackonionbot.config.immutable.api.Config;
import com.github.black0nion.blackonionbot.misc.SQLSetup;
import com.github.black0nion.blackonionbot.misc.exception.SQLSetupException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.metrics.prometheus.PrometheusMetricsTrackerFactory;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DatabaseConnection {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);

	private final HikariDataSource ds;

	private static DatabaseConnection instance;

	public static DatabaseConnection getInstance() {
		return instance;
	}

	public DatabaseConnection(Config config) {
		if (instance != null) instance.close();
		instance = this; // NOSONAR
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setJdbcUrl(config.getJdbcUrl());
		hikariConfig.setUsername(config.getPostgresUsername());
		hikariConfig.setPassword(config.getPostgresPassword());

		hikariConfig.setConnectionTimeout(2500);

		hikariConfig.setMetricsTrackerFactory(new PrometheusMetricsTrackerFactory());

		ds = new HikariDataSource(hikariConfig);

		// find all methods annotated with @SQLSetup and run them
		List<Method> methods = new Reflections(Main.class.getPackage().getName(), Scanners.MethodsAnnotated)
			.getMethodsAnnotatedWith(SQLSetup.class)
			.stream()
			// we don't care at what point the peek will be executed so the SonarLint warning can be ignored
			// also, in this case, I'm accessing my own code with reflections, so I know what I'm doing (I think)
			.peek(m -> m.setAccessible(true)) // NOSONAR
			.toList();

		logger.info("Found {} methods annotated with @SQLSetup, running them now...", methods.size());
		for (Method method : methods) {
			try {
				logger.debug("Running method {}#{}", method.getDeclaringClass().getName(), method.getName());
				method.invoke(null);
			} catch (Exception e) {
				throw new SQLSetupException("Error while running SQL setup method " + method.getName(),
					e.getCause() != null ? e.getCause() : e);
			}
		}
		logger.info("Finished running SQL setup methods.");
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
