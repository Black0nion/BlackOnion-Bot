package com.github.black0nion.blackonionbot.config.featureflags.impl;

import com.github.black0nion.blackonionbot.config.common.VariableLoader;
import com.github.black0nion.blackonionbot.utils.NotImplementedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class FeatureFlagFactoryImpl extends AbstractFeatureFlagFactory {

	public static final Path FF_PATH = Paths.get("files/featureflags.properties");
	private final Map<String, String> featureFlagsRaw = new HashMap<>();

	/**
	 * Loads the feature flags from (in that order, later overrides earlier):
	 * - the environment variables
	 * - the system properties
	 * - the file {@link #FF_PATH}
	 */
	public FeatureFlagFactoryImpl() throws IOException {
		if (Files.exists(FF_PATH)) {
			new VariableLoader(Pattern.compile("^([\\w._-]+)=(.*)$"))
				.loadVariables(mapToList(System.getenv()), null, featureFlagsRaw::put)
				.loadVariables(mapToList(System.getProperties()), null, featureFlagsRaw::put)
				.loadVariables(Files.readAllLines(FF_PATH), null, featureFlagsRaw::put);
		}
	}

	private List<String> mapToList(Map<?, ?> map) {
		return map.entrySet().stream().map(l -> l.getKey() + "=" + l.getValue()).toList();
	}

	@Override
	public <T extends AbstractFeatureFlag<?>> T createImpl(String key, Class<T> wantedFeatureFlag) {
		if (wantedFeatureFlag == BooleanFeatureFlag.class) {
			return wantedFeatureFlag.cast(new BooleanFeatureFlag(Boolean.parseBoolean(featureFlagsRaw.get(key))));
		}
		throw new NotImplementedException("Unknown feature flag type: " + wantedFeatureFlag);
	}
}
