package com.github.black0nion.blackonionbot.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.black0nion.blackonionbot.systems.logging.Logger;

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

    /**
     * Logs a message with the current log origin
     *
     * @param input The input to log
     */
    public void info(final String input) {
	Logger.log(LogMode.INFORMATION, this, input);
    }

    /**
     * Logs a message with the current log origin
     *
     * @param input The input to log
     */
    public void warn(final String input) {
	Logger.log(LogMode.WARNING, this, input);
    }

    /**
     * Logs a message with the current log origin
     *
     * @param input The input to log
     */
    public void error(final String input) {
	Logger.log(LogMode.ERROR, this, input);
    }

    /**
     * Logs a message with the current log origin
     *
     * @param input The input to log
     */
    public void fatal(final String input) {
	Logger.log(LogMode.FATAL, this, input);
    }
}