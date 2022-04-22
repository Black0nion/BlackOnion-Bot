package com.github.black0nion.blackonionbot;

import com.github.black0nion.blackonionbot.bot.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(final String[] args) {
		try {
			new Bot(args);
		} catch (final Throwable ex) {
			logger.error("Uncaught error occured", ex);
		}
	}
}