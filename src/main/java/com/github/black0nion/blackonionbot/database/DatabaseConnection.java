package com.github.black0nion.blackonionbot.database;

import com.github.black0nion.blackonionbot.Main;
import com.github.black0nion.blackonionbot.config.immutable.api.Config;
import com.github.black0nion.blackonionbot.misc.SQLSetup;
import com.github.black0nion.blackonionbot.misc.enums.RunMode;
import com.github.black0nion.blackonionbot.misc.exception.SQLSetupException;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.metrics.prometheus.PrometheusMetricsTrackerFactory;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class DatabaseConnection {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);

	private final HikariDataSource ds;
	private final HikariDataSource dsLowPriority;

	private static DatabaseConnection instance;

	public static DatabaseConnection getInstance() {
		return instance;
	}

	public DatabaseConnection(Config config) {
		if (instance != null) instance.close();
		instance = this; // NOSONAR

		HikariConfig hikariConfig = createHikariConfig(config);
		hikariConfig.setPoolName("MainPool");
		// in dev, we want to see possible bottlenecks
		// in prod, only real leaks are interesting
		hikariConfig.setLeakDetectionThreshold(config.getRunMode() == RunMode.DEV ? 5000 : 15000);

		ds = new HikariDataSource(hikariConfig);

		HikariConfig hikariConfigLowPriority = createHikariConfig(config);
		hikariConfigLowPriority.setPoolName("LowPriorityPool");

		dsLowPriority = new HikariDataSource(hikariConfigLowPriority);
		dsLowPriority.setMaximumPoolSize(5);


		// find all methods annotated with @SQLSetup and run them
		List<Method> methods = new Reflections(Main.class.getPackage().getName(), Scanners.MethodsAnnotated)
			.getMethodsAnnotatedWith(SQLSetup.class)
			.stream()
			// we don't care in which order the peek will be executed so the SonarLint warning can be ignored
			// also, in this case, I'm accessing my own code with reflections, so I know what I'm doing (I think)
			// (actually, I don't)
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

	private HikariConfig createHikariConfig(Config config) {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setJdbcUrl(config.getJdbcUrl());
		hikariConfig.setUsername(config.getPostgresUsername());
		hikariConfig.setPassword(config.getPostgresPassword());

		hikariConfig.setConnectionTimeout(2500);

		hikariConfig.setMetricsTrackerFactory(new PrometheusMetricsTrackerFactory());

		return hikariConfig;
	}

	public static Connection getLowPriorityConnection() throws SQLException {
		if (logger.isDebugEnabled())
			logger.debug("Low Priority Connection requested from: {}", Utils.stackTraceToString(Arrays.copyOfRange(Thread.currentThread().getStackTrace(), 0, 5)));
		return getInstance().dsLowPriority.getConnection();
	}

	public static Connection getConnection() throws SQLException {
		// TODO: feature flags for stack traces
		if (logger.isDebugEnabled())
			logger.debug("Connection requested from: {}", Utils.stackTraceToString(Arrays.copyOfRange(Thread.currentThread().getStackTrace(), 2, 7)));
		return instance.ds.getConnection();
	}

	public void close() {
		ds.close();
	}
}
