package com.github.black0nion.blackonionbot;

import com.github.black0nion.blackonionbot.bot.Bot;

public class Main {

	public static void main(String[] args) {
		try {
			new Bot().startBot();
		} catch (Exception ex) {
			System.err.println("UNCAUGHT ERROR:");
			ex.printStackTrace();
		}
	}

}
