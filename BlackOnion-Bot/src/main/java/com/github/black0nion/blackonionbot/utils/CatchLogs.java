/**
 * Date: 01.07.2021
 * Project: BlackOnion-Bot
 *
 * @author _SIM_
 */
package com.github.black0nion.blackonionbot.utils;

import java.io.PrintStream;

import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.systems.logging.Logger;

public class CatchLogs extends PrintStream {

    private static final PrintStream originalSystemOut = System.out;
    private static CatchLogs systemOutToLogger;

    public static void enableForClass(final Class<?> className) {
	systemOutToLogger = new CatchLogs(originalSystemOut, className.getName());
	System.setOut(systemOutToLogger);
    }

    public static void enableForPackage(final String packageToLog) {
	systemOutToLogger = new CatchLogs(originalSystemOut, packageToLog);
	System.setOut(systemOutToLogger);
    }

    public static void disable() {
	System.setOut(originalSystemOut);
	systemOutToLogger = null;
    }

    private final String packageOrClassToLog;

    private CatchLogs(final PrintStream original, final String packageOrClassToLog) {
	super(original);
	this.packageOrClassToLog = packageOrClassToLog;
    }

    @Override
    public void println(final String line) {
	final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
	final StackTraceElement caller = this.findCallerToLog(stack);
	if (caller == null) {
	    super.println(line);
	    return;
	}

	Logger.logInfo(line, LogOrigin.PLUGINS);
    }

    public StackTraceElement findCallerToLog(final StackTraceElement[] stack) {
	for (final StackTraceElement element : stack) {
	    if (element.getClassName().startsWith(this.packageOrClassToLog)) return element;
	}
	return null;
    }
}