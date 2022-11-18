package com.github.black0nion.blackonionbot.config.featureflags.impl;

import com.github.black0nion.blackonionbot.config.common.VariableLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class FeatureFlagFactoryImpl extends AbstractFeatureFlagFactory {

	public static final Path FF_PATH = Paths.get("files/featureflags.properties");
	private final Map<String, String> featureFlagsRaw = new HashMap<>();

	public FeatureFlagFactoryImpl() throws IOException {
		if (Files.exists(FF_PATH)) {
			new VariableLoader(Pattern.compile("^([\\w._-]+)=(.*)$")).loadVariables(Files.readAllLines(FF_PATH), null, featureFlagsRaw::put);
		}
	}

	@Override
	public <T extends AbstractFeatureFlag<?>> T createImpl(String key, Class<T> wantedFeatureFlag) {
		if (wantedFeatureFlag == BooleanFeatureFlag.class) {
			return wantedFeatureFlag.cast(new BooleanFeatureFlag(Boolean.parseBoolean(featureFlagsRaw.get(key))));
		}
		throw new IllegalArgumentException("Unknown feature flag type: " + wantedFeatureFlag);
	}
}
