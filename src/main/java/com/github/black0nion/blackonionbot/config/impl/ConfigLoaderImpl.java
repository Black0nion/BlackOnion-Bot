package com.github.black0nion.blackonionbot.config.impl;

import com.github.black0nion.blackonionbot.config.api.ConfigLoader;
import com.github.black0nion.blackonionbot.config.Flags;
import com.github.black0nion.blackonionbot.config.ConfigFlag;
import com.google.gson.internal.Primitives;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ConfigLoaderImpl implements ConfigLoader {

	public static final ConfigLoaderImpl INSTANCE = new ConfigLoaderImpl();

	@Override
	public <T> T get(String name, Class<T> clazz, ConfigFlag... flagsArr) {
		return getImpl(name, clazz, flagsArr);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getImpl(String name, Class<T> clazz, ConfigFlag... flagsArr) {
		name = name.toUpperCase(Locale.ROOT);
		final String value = System.getenv().containsKey(name) ? System.getenv(name) : System.getProperty(name);
		List<ConfigFlag> flags = flagsArr == null ? List.of() : List.of(flagsArr);
		if (value == null) {
			if (flags.contains(Flags.NonNull)) {
				throw new IllegalArgumentException("Missing required config value: " + name);
			}
			Flags.Default<T> defaultFlag = getFlag(flags, Flags.Default.class);
			return defaultFlag != null ? defaultFlag.defaultValue() : null;
		}
		T result;
		if (clazz.equals(String.class)) {
			result = (T) value;
		} else if (clazz.equals(Integer.class)) {
			result = (T) Integer.valueOf(value);
		} else if (clazz.equals(Long.class)) {
			result = (T) Long.valueOf(value);
		} else if (clazz.equals(Boolean.class)) {
			result = (T) Boolean.valueOf(value);
		} else if (clazz.equals(Double.class)) {
			result = (T) Double.valueOf(value);
		} else if (clazz.equals(Float.class)) {
			result = (T) Float.valueOf(value);
		} else {
			result = Primitives.wrap(clazz).cast(value);
		}
		for (ConfigFlag f : flags) {
			if (f instanceof Flags.MatchesRegex flag && !flag.regex().matcher(value).matches()) {
				throw new IllegalArgumentException("Config value " + name + " does not match regex " + flag.regex());
			} else if (f instanceof Flags.Range flag
				&& result instanceof Number num
				&& (num.doubleValue() < flag.min() || num.doubleValue() > flag.max())) {
				throw new IllegalArgumentException("Config value " + name + " is out of range " + flag.min() + " to " + flag.max());
			}
		}
		return result;
	}

	private static <T extends ConfigFlag> T getFlag(List<ConfigFlag> flags, @SuppressWarnings("SameParameterValue") Class<T> clazz) {
		return flags.stream()
			.filter(Objects::nonNull)
			.filter(f -> clazz.isAssignableFrom(f.getClass()))
			.map(clazz::cast)
			.findFirst()
			.orElse(null);
	}
}
