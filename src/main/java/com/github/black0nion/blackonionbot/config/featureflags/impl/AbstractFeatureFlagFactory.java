package com.github.black0nion.blackonionbot.config.featureflags.impl;

import com.github.black0nion.blackonionbot.config.featureflags.api.FeatureFlagFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractFeatureFlagFactory implements FeatureFlagFactory {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public <T extends AbstractFeatureFlag<?>> T create(String key, Class<T> wantedFeatureFlag) {
		T impl = createImpl(key, wantedFeatureFlag);
		logger.debug("Created feature flag {} with value {}", key, impl.getValue());
		return impl;
	}

	protected abstract <T extends AbstractFeatureFlag<?>> T createImpl(String key, Class<T> wantedFeatureFlag);
}
