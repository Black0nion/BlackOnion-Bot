/**
 * Date: 01.07.2021
 * Project: BlackOnion-Bot
 *
 * @author _SIM_
 */
package com.github.black0nion.blackonionbot.utils;

import java.io.PrintStream;

import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.systems.logging.Logger;
import com.github.black0nion.blackonionbot.systems.plugins.Caller;

public class CatchLogs extends PrintStream {

    private static final PrintStream originalSystemOut = System.out;
    private static final Pair<Caller, StackTraceElement> idk = new Pair<>(Caller.IDK, null);

    public static void disable() {
	System.setOut(originalSystemOut);
    }

    @Reloadable("catchlogs")
    public static void init() {
	System.setOut(new CatchLogs(originalSystemOut));
    }

    private CatchLogs(final PrintStream original) {
	super(original);
    }

    @Override
    public void println(final String line) {
	final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
	final Pair<Caller, StackTraceElement> callerPair = findCallerToLog(stack);
	final Caller caller = callerPair.getKey();
	if (caller == Caller.IDK) {
	    Logger.logInfo(line);
	} else if (caller == Caller.PLUGIN) {
	    Logger.logInfo("[" + callerPair.getValue().getClassName() + "] " + line, LogOrigin.PLUGINS);
	}
    }

    private static Pair<Caller, StackTraceElement> findCallerToLog(final StackTraceElement[] stack) {
	for (final StackTraceElement element : stack) {
	    // TODO: add check if it comes from a Plugin or something else
	    if (element.getMethodName().equalsIgnoreCase("onEnable") || element.getMethodName().equalsIgnoreCase("onDisable")) return new Pair<>(Caller.PLUGIN, element);
	}
	return idk;
    }
}