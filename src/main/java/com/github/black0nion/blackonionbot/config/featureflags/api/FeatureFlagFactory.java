package com.github.black0nion.blackonionbot.config.featureflags.api;

import com.github.black0nion.blackonionbot.config.featureflags.impl.AbstractFeatureFlag;
import com.github.black0nion.blackonionbot.config.featureflags.impl.BooleanFeatureFlag;

public interface FeatureFlagFactory {

	default BooleanFeatureFlag create(String key) {
		return create(key, BooleanFeatureFlag.class);
	}

	<T extends AbstractFeatureFlag<?>> T create(String key, Class<T> wantedFeatureFlag);
}
