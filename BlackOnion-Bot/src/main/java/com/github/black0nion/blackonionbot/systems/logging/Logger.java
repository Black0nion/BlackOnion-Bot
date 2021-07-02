package com.github.black0nion.blackonionbot.systems.logging;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.github.black0nion.blackonionbot.blackobjects.BlackLinkedList;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.misc.LogMode;
import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;

public class Logger {

    private static final SimpleDateFormat dtf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private static final HashMap<LogOrigin, BlackLinkedList<String>> logsPerCategory = new HashMap<>();
    private static final HashMap<LogMode, BlackLinkedList<String>> logsPerLevel = new HashMap<>();
    private static final List<String> logs = new LinkedList<>();

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
	    if (Bot.logLevel.contains(mode) && Bot.logOrigin.contains(origin)) {
		Bot.err.println(consoleLog);
	    }
	} else if (mode == LogMode.WARNING) {
	    consoleLog = "\033[33m" + log + "\033[0m";
	    if (Bot.logLevel.contains(mode) && Bot.logOrigin.contains(origin)) {
		Bot.out.println(consoleLog);
	    }
	} else {
	    consoleLog = "\033[94m" + log + "\033[0m";
	    if (Bot.logLevel.contains(mode) && Bot.logOrigin.contains(origin)) {
		Bot.out.println(consoleLog);
	    }
	}

	logs.add(consoleLog);

	if (logsPerCategory.containsKey(origin)) {
	    logsPerCategory.put(origin, logsPerCategory.get(origin).addAndGetSelf(consoleLog));
	} else {
	    logsPerCategory.put(origin, new BlackLinkedList<String>().addAndGetSelf(consoleLog));
	}

	if (logsPerLevel.containsKey(mode)) {
	    logsPerLevel.put(mode, logsPerLevel.get(mode).addAndGetSelf(consoleLog));
	} else {
	    logsPerLevel.put(mode, new BlackLinkedList<String>().addAndGetSelf(consoleLog));
	}

	try {
	    final File file = new File("files/logs/log." + origin.name().toLowerCase() + "/" + origin.name().toLowerCase() + "." + mode.name().toLowerCase() + ".log");
	    file.getParentFile().mkdirs();
	    file.createNewFile();
	    Files.asCharSink(file, StandardCharsets.UTF_8, FileWriteMode.APPEND).write(log + "\n");
	    Files.asCharSink(new File("files/logs/bot.log"), StandardCharsets.UTF_8, FileWriteMode.APPEND).write(log + "\n");
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
	if (logsPerCategory.containsKey(type)) {
	    System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" + "LOGS IN CATEGORY: " + type.name() + "\n" + String.join("\n", logsPerCategory.get(type)));
	} else {
	    System.out.println("No logs found for LogOrigin " + type.name());
	}
    }

    public static void printAll() {
	System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" + String.join("\n", logs));
    }

    /**
     * @param type
     * @param length
     */
    public static void printForCategory(final LogOrigin type, final int length) {
	if (logsPerCategory.containsKey(type)) {
	    final BlackLinkedList<String> logsInCat = logsPerCategory.get(type);
	    System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" + "PEAKING: " + type.name() + "\n" + String.join("\n", logsPerCategory.get(type).subList(0, (length > logsInCat.size() ? logsInCat.size() : length))));
	} else {
	    System.out.println("No logs found for LogOrigin " + type.name());
	}
    }

    /**
     * @param length
     */
    public static void printAll(final int length) {
	System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" + String.join("\n", logs.subList(0, (length > logs.size() ? logs.size() : length))));
    }

    /**
     * @param mode
     */
    public static void printForLevel(final LogMode mode) {
	if (logsPerLevel.containsKey(mode)) {
	    System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" + "PEAKING: " + mode.name() + "\n" + String.join("\n", logsPerLevel.get(mode)));
	} else {
	    System.out.println("No logs found for LogLevel " + mode.name());
	}
    }

    /**
     * @param mode
     * @param length
     */
    public static void printForLevel(final LogMode mode, final int length) {
	if (logsPerLevel.containsKey(mode)) {
	    final BlackLinkedList<String> logsInCat = logsPerLevel.get(mode);
	    System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" + "PEAKING: " + mode.name() + "\n" + String.join("\n", logsInCat.subList(0, (length > logsInCat.size() ? logsInCat.size() : length))));
	} else {
	    System.out.println("No logs found for LogLevel " + mode.name());
	}
    }
}