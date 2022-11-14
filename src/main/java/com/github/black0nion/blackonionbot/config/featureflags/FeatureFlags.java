package com.github.black0nion.blackonionbot.config.featureflags;

import com.github.black0nion.blackonionbot.config.featureflags.api.FeatureFlagFactory;
import com.github.black0nion.blackonionbot.config.featureflags.impl.BooleanFeatureFlag;
import com.github.black0nion.blackonionbot.misc.Holder;
import org.jetbrains.annotations.NotNull;

public class FeatureFlags extends Holder<FeatureFlagFactory> {


	public FeatureFlags(@NotNull FeatureFlagFactory factory) {
		super(factory);
	}

	public final BooleanFeatureFlag DB__LOG_CONNECTION_ACQUIRED = getValue().create("db.log_connection_acquired");
	public final BooleanFeatureFlag DB__LOG_CONNECTION_RELEASED = getValue().create("db.log_connection_released");
}
