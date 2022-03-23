package com.github.black0nion.blackonionbot.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum LogMode {
	INFORMATION,
	WARNING,
	ERROR,
	FATAL;

	private static final List<String> names = new ArrayList<>();

	public static String[] getNames() {
		if (names.size() != values().length) {
			names.clear();
			names.addAll(Arrays.stream(values()).map(LogMode::name).toList());
		}
		return names.toArray(new String[0]);
	}

	public static LogMode parse(final String input) {
		try {
			return valueOf(input.toUpperCase());
		} catch (final Exception e) {
			return null;
		}
	}
}