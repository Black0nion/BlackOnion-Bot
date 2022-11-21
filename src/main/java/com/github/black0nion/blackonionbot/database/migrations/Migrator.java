package com.github.black0nion.blackonionbot.database.migrations;

import com.github.black0nion.blackonionbot.config.featureflags.FeatureFlags;
import com.github.black0nion.blackonionbot.database.DatabaseConnector;
import com.github.black0nion.blackonionbot.database.SQLHelper;
import com.github.black0nion.blackonionbot.utils.ClasspathScanner;
import net.dv8tion.jda.internal.utils.Checks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Migrator {

	private static final Logger logger = LoggerFactory.getLogger(Migrator.class);
	private static final int CURRENT_VERSION = 1;
	// VERSION_DESCRIPTION-OF-THE-MIGRATION.sql
	private static final Pattern MIGRATION_FILES_PATTERN = Pattern.compile("^(\\d+)_(.*)\\.sql$");

	private final MigratorSQLHelperFactoryImpl sql;
	private final FeatureFlags featureFlags;

	public Migrator(DatabaseConnector connection, FeatureFlags featureFlags) throws SQLException {
		this.sql = new MigratorSQLHelperFactoryImpl(featureFlags, new MigratorConnectionGetter(connection));
		this.featureFlags = featureFlags;
	}

	public void migrate() {
		try {
			migrateImpl();
		} catch (MigrationException e) {
			throw e;
		} catch (Exception e) {
			throw new MigrationException("Failed to migrate database", e);
		}
	}

	private void migrateImpl() throws SQLException, IOException {
		logger.debug("Starting migration");
		sql.run("CREATE TABLE IF NOT EXISTS migrations (version INTEGER PRIMARY KEY NOT NULL)");

		if (alreadyAtLatestSchemaVersion()) return;

		ClasspathScanner classpathScanner = new ClasspathScanner();
		List<String> files = classpathScanner.getResourceFiles("database/migrations");
		if (files == null) {
			logger.info("No migrations found, skipping migration.");
			return;
		}

		boolean doIllegalFilesExist = files.stream()
			.filter(f -> !MIGRATION_FILES_PATTERN.matcher(f).matches())
			.peek(l -> logger.error("Migration File {} violates the pattern!", l)) // NOSONAR
			.findFirst()
			.isPresent();
		if (doIllegalFilesExist) throw new MigrationException("One or more migration files have errors!");

		// get the version from the database
		try (SQLHelper sqlHelper = sql.create("SELECT COALESCE(max(version), 0) AS version FROM migrations"); ResultSet rs = sqlHelper.executeQuery()) {
			if (rs.next()) {
				int databaseVersion = rs.getInt("version");
				if (databaseVersion != 0) {
					if (databaseVersion > CURRENT_VERSION) {
						throw new MigrationException("Database version is higher than the current version of the bot!");
					}

					for (String file : files) {
						Matcher fileNameMatcher = MIGRATION_FILES_PATTERN.matcher(file);
						Checks.check(fileNameMatcher.matches(), "File name does not match pattern!");

						float fileVersion = Float.parseFloat(fileNameMatcher.group(1));
						if (databaseVersion <= fileVersion) {
							logger.info("Executing migration stored in {} which migrates from {}", file, fileVersion);
							// execute the file
							executeSqlFile(classpathScanner.getResourceAsStream("database/migrations/" + file));
						} else {
							logger.debug("Skipping migration stored in {} which migrates from {}", file, fileVersion);
						}
					}
				}
			} else {
				logger.info("No database version found (= no existing tables), skipping any migrations");
			}

			logger.debug("Inserting new version ({}) into database", CURRENT_VERSION);
			sql.run("INSERT INTO migrations (version) VALUES (?) ON CONFLICT DO NOTHING", CURRENT_VERSION);

			logger.debug("Migrations finished, committing changes...");
			sql.commit();
			logger.info("Migration committed.");
		} catch (Exception ex) {
			sql.revert();
			throw ex;
		} finally {
			// finally, close the connection used for migrations
			logger.debug("Closing connection used for migrations...");
			sql.getSupplier().get().close();
			logger.debug("Connection closed.");
		}
	}

	private boolean alreadyAtLatestSchemaVersion() throws SQLException {
		try (SQLHelper sq1 = sql.create("SELECT version FROM migrations ORDER BY version DESC LIMIT 1");
				ResultSet rs = sq1.executeQuery()) {
			if (rs.next()) {
				int version = rs.getInt("version");
				logger.info("DB version is {}, local version is {}", version, CURRENT_VERSION);
				if (version == CURRENT_VERSION) {
					logger.info("Already at latest schema version, skipping migration.");
					return true;
				}
			}
		}
		return false;
	}

	private void executeSqlFile(InputStream stream) throws SQLException, IOException {
		try (stream) {
			logger.debug("Executing SQL file");
			StringBuilder lines = new StringBuilder();
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			while ((line = reader.readLine()) != null) {
				lines.append(line);
				if (line.startsWith("--")) continue;
				if (line.endsWith(";")) {
					if (featureFlags.db_logMigrationCommands.getValue())
						logger.debug("Running migration command: {}", lines);

					try {
						sql.run(lines.toString());
					} finally {
						lines = new StringBuilder();
					}
				}
			}
		}
	}
}
