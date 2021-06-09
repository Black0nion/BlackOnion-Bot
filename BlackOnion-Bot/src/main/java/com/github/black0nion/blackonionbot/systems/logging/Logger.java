package com.github.black0nion.blackonionbot.systems.logging;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.github.black0nion.blackonionbot.misc.LogMode;
import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;

public class Logger {
	
	private static final SimpleDateFormat dtf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	public static void log(final LogMode mode, final LogOrigin origin, final String logInput) {
		//StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[Thread.currentThread().getStackTrace().length - 1];
		//String log = dtf.format(now) + "[" + stackTraceElement.getFileName().replace(".java", "") + "." + stackTraceElement.getMethodName() + ":" + stackTraceElement.getLineNumber() + "] [" + mode.name() + "] " + logInput;
		final String log = dtf.format(new Date()) + " [" + origin.name() + "] [" + mode.name() + "] " + logInput;
		if (mode == LogMode.ERROR || mode == LogMode.FATAL)
			System.err.println(log);
		else if (mode == LogMode.WARNING)
			System.out.println("\033[33m" + log + "\033[0m");
		else
			System.out.println("\033[94m" + log + "\033[0m");
		
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
}