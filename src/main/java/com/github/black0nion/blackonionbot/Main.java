package com.github.black0nion.blackonionbot;

import com.github.black0nion.blackonionbot.bot.Bot;

public class Main {

    public static void main(final String[] args) {
	try {
	    new Bot().startBot();
	} catch (final Exception ex) {
	    System.err.println("UNCAUGHT ERROR:");
	    ex.printStackTrace();
	}
    }

}
