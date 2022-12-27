package com.github.black0nion.blackonionbot.database.migrations;

import com.github.black0nion.blackonionbot.config.featureflags.FeatureFlags;
import com.github.black0nion.blackonionbot.database.SQLHelper;
import com.github.black0nion.blackonionbot.database.helpers.impl.SQLHelperFactoryImpl;

import java.sql.SQLException;

public class MigratorSQLHelperFactoryImpl extends SQLHelperFactoryImpl {

	private final MigratorConnectionGetter connectionGetter;

	public MigratorSQLHelperFactoryImpl(FeatureFlags featureFlags, MigratorConnectionGetter connectionGetter) {
		super(featureFlags, connectionGetter.getSupplier());
		this.connectionGetter = connectionGetter;
	}

	@Override
	public SQLHelper create(String rawSQL, Object... parameters) throws SQLException {
		return new MigrationsSQLHelper(this.featureFlags.db_logConnectionReleased.getValue(), this.connectionGetter.getSupplier(), rawSQL, parameters);
	}

	public void commit() throws SQLException {
		connectionGetter.getSupplier().get().commit();
	}

	public void revert() throws SQLException {
		connectionGetter.getSupplier().get().rollback();
	}
}
