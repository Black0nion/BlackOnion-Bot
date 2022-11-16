package com.github.black0nion.blackonionbot.database.migrations;

import com.github.black0nion.blackonionbot.config.immutable.api.Config;
import com.github.black0nion.blackonionbot.database.DatabaseConnector;
import com.github.black0nion.blackonionbot.database.SQLHelper;
import com.github.black0nion.blackonionbot.database.SQLHelperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class Migrator {

	private static final Logger logger = LoggerFactory.getLogger(Migrator.class);

	private static final float CURRENT_VERSION = 1;

	private final DatabaseConnector databaseConnector;
	private final Config config;
	private final SQLHelperFactory sql;

	public Migrator(DatabaseConnector connection, Config config) {
		this.databaseConnector = connection;
		this.config = config;
		this.sql = this.databaseConnector.getSqlHelperFactory();
	}

	public void migrate() throws SQLException, IOException {
		try {
			migrateImpl();
		} catch (SQLException ex) {
			// restore schema from backup
			throw new SQLException("Failed to migrate database", ex);
		}
	}

	private void migrateImpl() throws SQLException, IOException {
		sql.run("CREATE TABLE IF NOT EXISTS migrations (version FLOAT PRIMARY KEY NOT NULL)");

		if (alreadyAtLatestSchemaVersion()) return;

		// check if migrations schema exists
		// if yes, check if the versions match
		// 	-> if not, backup schema
		// if not, backup schema
		boolean backupExists;

		try (SQLHelper sqlHelper = sql.create("""
				
				);
				"""); ResultSet resultSet = sqlHelper.executeQuery()) {
			resultSet.next();
			backupExists = resultSet.next() && Objects.equals(resultSet.getString(1), "1");
		}

		if (backupExists) {
			logger.debug("Backup exists, checking if versions match");
			// check if backup schema exists and has same version as current schema
			try (SQLHelper sq1 = sql.create(getHashSQL("migrations")); ResultSet rs1 = sq1.executeQuery();
				 SQLHelper sq2 = sql.create(getHashSQL("backup.migrations")); ResultSet rs2 = sq2.executeQuery()) {
				if (rs1.next() && rs2.next()) {
					String hash1 = rs1.getString(1);
					String hash2 = rs2.getString(1);
					if (!hash1.equals(hash2)) {
						logger.debug("Backup schema has different version, backing up current schema");
						backupSchema();
					}
				}
			}
		} else {
			logger.debug("Backup does not exist, backing up current schema");
			backupSchema();
		}

		// read all files in the jar file
		// the folder is called database/migrations
		// the files are called 1.sql, 1.1.sql, 1.4.sql, ...
		// the files are executed in order
		// if the version from the database is lower than the version of the file, execute it
		// if the version from the database is higher than the version of the file, ignore it
		// if the version from the database is equal to the version of the file, ignore it
		// after executing all files, update the version in the database
		File[] files = new File("database/migrations").listFiles();
		if (files == null) return;

		// get the version from the database
		try (SQLHelper sqlHelper = sql.create("SELECT max(version) FROM migrations"); ResultSet rs = sqlHelper.executeQuery()) {
			if (rs.next()) {
				float version = rs.getFloat("version");
				for (File file : files) {
					float fileVersion = Float.parseFloat(file.getName().replace(".sql", ""));
					if (version < fileVersion) {
						// execute the file
						executeSqlFile(file);
					}
				}
			}
			sql.run("INSERT INTO migrations (version) VALUES (?)", CURRENT_VERSION);
		}
	}

	private void backupSchema() throws SQLException {
		CloneSchema.addFunction(config, sql);
		sql.run("SELECT clone_schema('public', 'backup')");
		if (true) return;
		try (SQLHelper sqlHelper = sql.create("SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'");
				ResultSet rs = sqlHelper.executeQuery()) {
			while (rs.next()) {
				String tableName = rs.getString("table_name");
				sql.run("CREATE SCHEMA IF NOT EXISTS backup");
				sql.run("DROP TABLE IF EXISTS backup." + tableName);
				sql.run("CREATE TABLE backup." + tableName + " AS TABLE " + tableName);
			}
		}
		logger.debug("Backup complete");
	}

	private boolean alreadyAtLatestSchemaVersion() throws SQLException {
		try (SQLHelper sq1 = sql.create("SELECT version FROM migrations ORDER BY version DESC LIMIT 1");
				ResultSet rs = sq1.executeQuery()) {
			if (rs.next()) {
				float version = rs.getFloat(1);
				if (version == CURRENT_VERSION) {
					return true;
				}
			}
		}
		return false;
	}

	private void executeSqlFile(File file) throws SQLException, IOException {
		List<String> lines = Files.readAllLines(file.toPath());
		for (String line : lines) {
			if (line.startsWith("--")) continue;
			if (line.endsWith(";")) {
				try (Connection rs = databaseConnector.retrieveConnection(false); PreparedStatement ps = rs.prepareStatement(line)) {
					ps.executeUpdate();
				}
			}
		}
	}

	private String getHashSQL(String s) {
		return "SELECT " +
			"md5(CAST((array_agg(f.* order by version)) AS text))" +
			"FROM " +
			s + " f;";
	}
}
