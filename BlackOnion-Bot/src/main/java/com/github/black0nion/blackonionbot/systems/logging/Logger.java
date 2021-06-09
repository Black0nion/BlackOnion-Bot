package com.github.black0nion.blackonionbot.systems.logging;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.github.black0nion.blackonionbot.blackobjects.BlackArrayList;
import com.github.black0nion.blackonionbot.misc.LogMode;
import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;

public class Logger {

    private static final SimpleDateFormat dtf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private static final HashMap<LogOrigin, BlackArrayList<String>> logsPerCategory = new HashMap<>();
    private static final List<String> logs = new ArrayList<>();

    public static void log(final LogMode mode, final LogOrigin origin, final String logInput) {
	// StackTraceElement stackTraceElement =
	// Thread.currentThread().getStackTrace()[Thread.currentThread().getStackTrace().length
	// - 1];
	// String log = dtf.format(now) + "[" +
	// stackTraceElement.getFileName().replace(".java", "") + "." +
	// stackTraceElement.getMethodName() + ":" + stackTraceElement.getLineNumber() +
	// "] [" + mode.name() + "] " + logInput;
	final String log = dtf.format(new Date()) + " [" + origin.name() + "] [" + mode.name() + "] " + logInput;
	String consoleLog = log;
	if (mode == LogMode.ERROR || mode == LogMode.FATAL) {
	    consoleLog = "\033[91m" + log + "\033[0m";
	    System.err.println(consoleLog);
	} else if (mode == LogMode.WARNING) {
	    consoleLog = "\033[33m" + log + "\033[0m";
	    System.out.println(consoleLog);
	} else {
	    consoleLog = "\033[94m" + log + "\033[0m";
	    System.out.println(consoleLog);
	}

	logs.add(consoleLog);
	if (logsPerCategory.containsKey(origin)) {
	    logsPerCategory.put(origin, logsPerCategory.get(origin).addAndGetSelf(consoleLog));
	} else {
	    logsPerCategory.put(origin, new BlackArrayList<String>().addAndGetSelf(consoleLog));
	}

	try {
	    final File file = new File("files/logs/log." + origin.name().toLowerCase() + "/" + origin.name().toLowerCase() + "." + mode.name().toLowerCase() + ".log");
	    file.getParentFile().mkdirs();
	    file.createNewFile();
	    Files.asCharSink(file, StandardCharsets.UTF_8, FileWriteMode.APPEND).write(log);
	    Files.asCharSink(new File("files/logs/bot.log"), StandardCharsets.UTF_8, FileWriteMode.APPEND).write(log);
	} catch (final Exception e) {
	    e.printStackTrace();
	}
    }

    public static void logInfo(final String logInput, final LogOrigin origin) {
	log(LogMode.INFORMATION, origin, logInput);
    }

    public static void logWarning(final String logInput, final LogOrigin origin) {
	log(LogMode.WARNING, origin, logInput);
    }

    public static void logError(final String logInput, final LogOrigin origin) {
	log(LogMode.ERROR, origin, logInput);
    }

    public static void log(final LogMode mode, final String logInput) {
	log(mode, LogOrigin.BOT, logInput);
    }

    public static void logInfo(final String logInput) {
	log(LogMode.INFORMATION, LogOrigin.BOT, logInput);
    }

    public static void logWarning(final String logInput) {
	log(LogMode.WARNING, LogOrigin.BOT, logInput);
    }

    public static void logError(final String logInput) {
	log(LogMode.ERROR, LogOrigin.BOT, logInput);
    }

    /**
     * @param type
     */
    public static void printForCategory(final LogOrigin type) {
	System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" + "PEAKING: " + type.name() + "\n" + String.join("\n", logsPerCategory.get(type)));
    }

    public static void printAll() {
	System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" + String.join("\n", logs));
    }
}