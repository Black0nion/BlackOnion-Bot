package com.github.black0nion.blackonionbot.config.featureflags;

import com.github.black0nion.blackonionbot.config.featureflags.api.FeatureFlagFactory;
import com.github.black0nion.blackonionbot.config.featureflags.impl.BooleanFeatureFlag;
import com.github.black0nion.blackonionbot.misc.Holder;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("CheckStyle")
public class FeatureFlags extends Holder<FeatureFlagFactory> {


	public FeatureFlags(@NotNull FeatureFlagFactory factory) {
		super(factory);
	}

	public final BooleanFeatureFlag bot_shutdownBeforeConnection = getValue().create("bot.shutdownBeforeConnection");

	public final BooleanFeatureFlag stats_logCollection = getValue().create("stats.logCollection");
	public final BooleanFeatureFlag stats_logCountRefresh = getValue().create("stats.logCountRefresh");

	public final BooleanFeatureFlag db_logConnectionAcquired = getValue().create("db.log_connection_acquired");
	public final BooleanFeatureFlag db_logConnectionReleased = getValue().create("db.log_connection_released");
	public final BooleanFeatureFlag db_logMigrationCommands = getValue().create("db.log_migration_commands");
}
