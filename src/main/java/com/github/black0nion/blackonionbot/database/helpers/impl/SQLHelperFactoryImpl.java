package com.github.black0nion.blackonionbot.database.helpers.impl;

import com.github.black0nion.blackonionbot.config.featureflags.FeatureFlags;
import com.github.black0nion.blackonionbot.database.ConnectionSupplier;
import com.github.black0nion.blackonionbot.database.SQLHelper;
import com.github.black0nion.blackonionbot.database.helpers.api.SQLHelperFactory;

import java.sql.SQLException;

/**
 * A very generic implementation of {@link SQLHelperFactory} that uses the passed {@link ConnectionSupplier} to create {@link SQLHelper} instances
 */
public class SQLHelperFactoryImpl implements SQLHelperFactory {

	protected final FeatureFlags featureFlags;
	private final ConnectionSupplier supplier;

	public SQLHelperFactoryImpl(FeatureFlags featureFlags, ConnectionSupplier supplier) {
		this.featureFlags = featureFlags;
		this.supplier = supplier;
	}

	public ConnectionSupplier getSupplier() {
		return supplier;
	}

	@Override
	public SQLHelper create(String rawSQL, Object... parameters) throws SQLException {
		return new SQLHelper(featureFlags.db_logConnectionReleased.getValue(), this.supplier, rawSQL, parameters);
	}
}
