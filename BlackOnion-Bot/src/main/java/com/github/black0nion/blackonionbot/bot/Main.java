package com.github.black0nion.blackonionbot.bot;

public class Main {

	public static void main(String[] args) {
		try {
			Bot.startBot();
		} catch (Exception ex) {
			System.err.println("UNCAUGHT ERROR:");
			ex.printStackTrace();
		}
	}

}
