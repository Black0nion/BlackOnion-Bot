package com.github.black0nion.blackonionbot.bot;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.github.black0nion.blackonionbot.enums.RunMode;
import com.github.black0nion.blackonionbot.utils.FileUtils;
import com.google.common.io.Files;

public class BotSecrets {
	
	public static void init() {
		try {
			bot_token = (Bot.runMode == RunMode.RELEASE ? FileUtils.readArrayListFromFile("token").get(0) : FileUtils.readArrayListFromFile("token").get(1));
		} catch (Exception e) {
			if (e instanceof IndexOutOfBoundsException) {
				System.out.println("[BOT] Please type in a second line with a valid token for the development bot!");
				System.exit(0);
			} else {
				e.printStackTrace();
			}
		}
	}
	public static String bot_token;
	
	private static ArrayList<String> botAdmins = new ArrayList<>();
	
	static {
		try {
			if (new File("files/discordusers").exists()) {
				for (String line : Files.readLines(new File("files/discordusers"), StandardCharsets.UTF_8)) {
					botAdmins.add(line);
				}
			}
		} catch (IOException ex) {ex.printStackTrace();}
	}
	
	public static boolean isAdmin(long userId) {
		for (String user : botAdmins) {
			if (user.equals(String.valueOf(userId)))
				return true;
		}
		
		return false;
	}

	public static boolean isDiscordUser(String code) {
		return false;
	}
}
