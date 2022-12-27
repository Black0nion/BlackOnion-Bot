package com.github.black0nion.blackonionbot.database;

import com.github.black0nion.blackonionbot.Main;
import com.github.black0nion.blackonionbot.config.featureflags.FeatureFlags;
import com.github.black0nion.blackonionbot.config.immutable.api.Config;
import com.github.black0nion.blackonionbot.database.helpers.api.SQLHelperFactory;
import com.github.black0nion.blackonionbot.database.helpers.impl.SQLHelperFactoryImpl;
import com.github.black0nion.blackonionbot.database.migrations.Migrator;
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

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseConnector {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseConnector.class);

	private final HikariDataSource ds;
	private final HikariDataSource dsLowPriority;

	private final FeatureFlags featureFlags;

	private final SQLHelperFactory sqlHelperFactory;

	public DatabaseConnector(Config config, FeatureFlags featureFlags) throws SQLException, IOException {
		this.featureFlags = featureFlags;
		this.sqlHelperFactory = new SQLHelperFactoryImpl(featureFlags, this::getConnection);

		HikariConfig hikariConfig = createHikariConfig(config);
		hikariConfig.setPoolName("MainPool");
		// in dev, we want to see possible bottlenecks
		// in prod, only real leaks are interesting
		hikariConfig.setLeakDetectionThreshold(config.getRunMode() == RunMode.DEV ? 5000 : 15000);

		logger.debug("Starting MainPool...");
		ds = new HikariDataSource(hikariConfig);
		logger.debug("Started MainPool!");

		HikariConfig hikariConfigLowPriority = createHikariConfig(config);
		hikariConfigLowPriority.setPoolName("LowPriorityPool");

		hikariConfigLowPriority.setMaximumPoolSize(5);

		logger.debug("Starting LowPriorityPool...");
		dsLowPriority = new HikariDataSource(hikariConfigLowPriority);
		logger.debug("Started LowPriorityPool!");

		new Migrator(this, featureFlags).migrate();

		runSqlSetupMethods();
	}

	private void runSqlSetupMethods() {
		// find all methods annotated with @SQLSetup and run them
		// NOSONAR
		List<Method> methods = new Reflections(Main.class.getPackage().getName(), Scanners.MethodsAnnotated)
			.getMethodsAnnotatedWith(SQLSetup.class)
			.stream()
			// we don't care in which order the peek will be executed so the SonarLint warning can be ignored
			// also, in this case, I'm accessing my own code with reflections, so I know what I'm doing (I think)
			// (actually, I don't)
			.peek(m -> m.setAccessible(true)) // NOSONAR
			// sort to make sure that the methods dependencies are satisfied at the point of execution
			// (e.g. if method A depends on something done in method B, method B will be executed before method A)
			.sorted(SQLSetupComparator.INSTANCE)
			.toList();

		logger.debug("Running SQL setup methods in the following order: {}", methods.stream() // NOSONAR what does that even mean
				.map(m -> m.getDeclaringClass().getSimpleName() + "." + m.getName())
				.collect(Collectors.joining(", ")));

		logger.info("Found {} methods annotated with @SQLSetup, running them now...", methods.size());
		for (Method method : methods) {
			try {
				logger.debug("Running method {}#{}", method.getDeclaringClass().getName(), method.getName());
				method.invoke(null, sqlHelperFactory);
			} catch (Exception e) {
				throw new SQLSetupException("Error while running SQL setup method " + method.getDeclaringClass().getName() + "#" + method.getName(),
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

	public Connection retrieveConnection(boolean lowPriority) {
		try {
			return lowPriority ? getLowPriorityConnection() : getConnection();
		} catch (SQLException e) {
			logger.error("Error while getting connection from pool", e);
			return null;
		}
	}

	Connection getConnection() throws SQLException {
		// check for the feature flag first because that's (probably) faster
		if (featureFlags.db_logConnectionAcquired.getValue() && logger.isDebugEnabled())
			logger.debug("Connection requested from: {}", Utils.stackTraceToString(Arrays.copyOfRange(Thread.currentThread().getStackTrace(), 2, 10)));
		return ds.getConnection();
	}

	public Connection getLowPriorityConnection() throws SQLException {
		if (featureFlags.db_logConnectionAcquired.getValue() && logger.isDebugEnabled())
			logger.debug("Low Priority Connection requested from: {}", Utils.stackTraceToString(Arrays.copyOfRange(Thread.currentThread().getStackTrace(), 0, 8)));
		return dsLowPriority.getConnection();
	}

	public void close() {
		ds.close();
		dsLowPriority.close();
		logger.info("Closed all connection pools.");
	}

	public SQLHelperFactory getSqlHelperFactory() {
		return sqlHelperFactory;
	}
}
