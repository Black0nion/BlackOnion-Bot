package com.github.black0nion.blackonionbot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.github.black0nion.blackonionbot.misc.LogMode;
import com.github.black0nion.blackonionbot.misc.LogOrigin;

public class Logger {
	
	private static final SimpleDateFormat dtf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss ");
	
	public static void log(LogMode mode, LogOrigin origin, String logInput) {
		//StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[Thread.currentThread().getStackTrace().length - 1];
		//String log = dtf.format(now) + "[" + stackTraceElement.getFileName().replace(".java", "") + "." + stackTraceElement.getMethodName() + ":" + stackTraceElement.getLineNumber() + "] [" + mode.name() + "] " + logInput;
		String log = dtf.format(new Date()) + "[" + origin.name() + "] [" + mode.name() + "] " + logInput;
		if (mode == LogMode.ERROR) {
			System.err.println(log);
		} else {
			System.out.println(log);
		}
		try {
			File dir = new File("files/logs/log." + origin.name().toLowerCase());
			File file = new File("files/logs/log." + origin.name().toLowerCase() + "/" + origin.name().toLowerCase() + "." + mode.name().toLowerCase() + ".log");
			dir.mkdirs();
			file.createNewFile();
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("files/logs/log." + origin.name().toLowerCase() + "/" + origin.name().toLowerCase() + "." + mode.name().toLowerCase() + ".log", true)));
		    out.println(log);
		    out.close();
		    PrintWriter out2 = new PrintWriter(new BufferedWriter(new FileWriter("files/logs/log", true)));
		    out2.println(log);
		    out2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void logInfo(String logInput, LogOrigin origin) {
		log(LogMode.INFORMATION, origin, logInput);
	}
	
	public static void logWarning(String logInput, LogOrigin origin) {
		log(LogMode.WARNING, origin, logInput);
	}
	
	public static void logError(String logInput, LogOrigin origin) {
		log(LogMode.ERROR, origin, logInput);
	}
	
	public static void log(LogMode mode, String logInput) {
		log(mode, LogOrigin.API, logInput);
	}
	
	public static void logInfo(String logInput) {
		log(LogMode.INFORMATION, LogOrigin.API, logInput);	
	}
	
	public static void logWarning(String logInput) {
		log(LogMode.WARNING, LogOrigin.API, logInput);
	}
	
	public static void logError(String logInput) {
		log(LogMode.ERROR, LogOrigin.BOT, logInput);
	}
}
