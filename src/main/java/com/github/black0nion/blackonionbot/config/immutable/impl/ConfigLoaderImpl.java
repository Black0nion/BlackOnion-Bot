package com.github.black0nion.blackonionbot.config.immutable.impl;

import com.github.black0nion.blackonionbot.config.common.exception.ConfigLoadingException;
import com.github.black0nion.blackonionbot.config.common.ConfigFlag;
import com.github.black0nion.blackonionbot.config.common.Flags;
import com.github.black0nion.blackonionbot.config.common.ConfigLoader;
import com.github.black0nion.blackonionbot.utils.Utils;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ConfigLoaderImpl implements ConfigLoader {

	public static final ConfigLoaderImpl INSTANCE = new ConfigLoaderImpl();

	private ConfigLoaderImpl() {}

	@Override
	public <T> T get(String name, Class<T> clazz, ConfigFlag... flagsArr) {
		try {
			return loadImpl(name, clazz, flagsArr);
		} catch (Exception e) {
			throw new ConfigLoadingException("Failed to load config value for " + name, e);
		}
	}

	public static <T> T loadImpl(String name, Class<T> clazz, ConfigFlag... flagsArr) {
		name = name.toUpperCase(Locale.ROOT);
		final String value = System.getenv().containsKey(name) ? System.getenv(name) : System.getProperty(name);
		return parse(name, value, clazz, flagsArr);
	}

	public static <T> T parse(String name, String value, Class<T> clazz, ConfigFlag... flagsArr) {
		List<ConfigFlag> flags = flagsArr == null ? List.of() : List.of(flagsArr);
		if (value == null) {
			if (flags.contains(Flags.NonNull)) {
				throw new IllegalArgumentException("Missing required config value: " + name);
			}
			@SuppressWarnings("unchecked")
			Flags.Default<T> defaultFlag = getFlag(flags, Flags.Default.class);
			return defaultFlag != null ? defaultFlag.defaultValue() : null;
		}
		T result = Utils.parseToT(value, clazz);
		for (ConfigFlag f : flags) {
			if (f instanceof Flags.MatchesRegex flag && !flag.regex().matcher(value).matches()) {
				throw new IllegalArgumentException("Config value " + name + " does not match regex " + flag.regex() + " (is '" + value + "')");
			} else if (f instanceof Flags.Range flag
				&& result instanceof Number num
				&& (num.doubleValue() < flag.min() || num.doubleValue() > flag.max())) {
				throw new IllegalArgumentException("Config value " + name + " is out of range " + flag.min() + " to " + flag.max());
			}
		}
		return result;
	}

	public static <T extends ConfigFlag> T getFlag(List<ConfigFlag> flags, @SuppressWarnings("SameParameterValue") Class<T> clazz) {
		return flags.stream()
			.filter(Objects::nonNull)
			.filter(f -> clazz.isAssignableFrom(f.getClass()))
			.map(clazz::cast)
			.findFirst()
			.orElse(null);
	}
}
