package com.github.black0nion.blackonionbot.config.common;

public interface ConfigLoader {

	ConfigFlag[] EMPTY_FLAGS_ARR = new ConfigFlag[0];

	@SuppressWarnings("SameParameterValue")
	default <T> T get(String name, Class<T> clazz) {
		return get(name, clazz, EMPTY_FLAGS_ARR);
	}

	<T> T get(String name, Class<T> clazz, ConfigFlag... flagsArr);
}
