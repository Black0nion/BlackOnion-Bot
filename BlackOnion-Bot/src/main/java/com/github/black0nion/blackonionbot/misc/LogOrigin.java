package com.github.black0nion.blackonionbot.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum LogOrigin {
    API, BOT, DISCORD, MONGODB, OTHER, INFLUX_DB, DASHBOARD, PLUGINS;

    private static final List<String> names = new ArrayList<>();

    public static final String[] getNames() {
	if (names.size() != values().length) {
	    names.clear();
	    names.addAll(Arrays.asList(values()).stream().map(LogOrigin::name).collect(Collectors.toList()));
	}
	return names.toArray(new String[0]);
    }

    public static final LogOrigin parse(final String input) {
	try {
	    return valueOf(input.toUpperCase());
	} catch (final Exception e) {
	    return null;
	}
    }
}