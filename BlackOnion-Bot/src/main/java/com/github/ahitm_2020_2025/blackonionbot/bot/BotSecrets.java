package com.github.ahitm_2020_2025.blackonionbot.bot;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.github.ahitm_2020_2025.blackonionbot.enums.BotRole;
import com.github.ahitm_2020_2025.blackonionbot.enums.RunMode;
import com.github.ahitm_2020_2025.blackonionbot.utils.BotUser;
import com.github.ahitm_2020_2025.blackonionbot.utils.DiscordUser;
import com.github.ahitm_2020_2025.blackonionbot.utils.FileUtils;
import com.google.common.io.Files;

public class BotSecrets {
	
	public static BotUser counterUser;
	
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
	
	private static ArrayList<BotUser> botUsers = new ArrayList<>();
	private static ArrayList<DiscordUser> discordUsers = new ArrayList<>();
	
	static {
		//not very secure, I know
		counterUser = new BotUser("counter", "cuntUser", BotRole.ADMIN);
		System.out.println(counterUser.getName() + ":" + counterUser.getPasssword());
		botUsers.add(new BotUser("SIMULATAN", "simugamz1@gmail.com", "sim", BotRole.ADMIN));
		botUsers.add(new BotUser("ManuelP", "mani", BotRole.ADMIN));
		botUsers.add(new BotUser("test", "tester", BotRole.USER));
		botUsers.add(counterUser);
		
		try {
			if (new File("files/discordusers").exists()) {
				for (String line : Files.readLines(new File("files/discordusers"), StandardCharsets.UTF_8)) {
					String[] lineSplitted = line.split(":");
					discordUsers.add(new DiscordUser(Long.valueOf(lineSplitted[0]), Boolean.valueOf(lineSplitted[1])));
				}
			}
		} catch (IOException ex) {}
	}
	
	public static boolean isAdmin(String username, String password) {
		if (credentialsRight(username, password)) {
			return getUserByCredentials(username, password).isAdmin();
		}
		
		return false;
	}
	
	public static boolean isAdmin(long userId) {
		for (DiscordUser user : discordUsers) {
			if (user.getUserId() == userId)
				return user.isAdmin();
		}
		
		return false;
	}
	
	public static boolean credentialsRight(String username, String password) {
		return getUserByCredentials(username, password) != null;
	}
	
	@Deprecated
	/**
	 * ATTENTION: WILL RETURN "NULL" IF THERE'S NO USER MATCHING!
	 * @param username
	 * @param password
	 * @return The user, or null if there isn't any user with that credentials.
	 */
	public static BotUser getUserByCredentials(String username, String password) {
		for (BotUser user : botUsers) {
			if ((user.getName().equalsIgnoreCase(username) || (user.getEmail() != null && user.getEmail().equalsIgnoreCase(username))) && user.getPasssword().equals(password))
				return user;
		}
		return null;
	}
}
