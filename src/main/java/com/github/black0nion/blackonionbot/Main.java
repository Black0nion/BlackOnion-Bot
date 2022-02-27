package com.github.black0nion.blackonionbot;

import com.github.black0nion.blackonionbot.bot.Bot;

public class Main {

	public static void main(final String[] args) {
		try {
			Bot.startBot(args);
		} catch (final Throwable ex) {
			System.err.print("UNCAUGHT ERROR: ");
			ex.printStackTrace();
		}
	}
}