package com.github.ahitm_2020_2025.blackonionbot.bot;

import java.util.ArrayList;

import com.github.ahitm_2020_2025.blackonionbot.enums.BotRole;
import com.github.ahitm_2020_2025.blackonionbot.enums.RunMode;
import com.github.ahitm_2020_2025.blackonionbot.utils.BotUser;
import com.github.ahitm_2020_2025.blackonionbot.utils.FileUtils;

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
	
	private static ArrayList<BotUser> users = new ArrayList<>();
	
	static {
		//not very secure, I know
		users.add(new BotUser("SIMULATAN", "simugamz1@gmail.com", "sim", BotRole.ADMIN));
		users.add(new BotUser("ManuelP", "mani", BotRole.ADMIN));
		users.add(new BotUser("test", "tester", BotRole.USER));
	}
	
	public static boolean isAdmin(String username, String password) {
		if (credentialsRight(username, password)) {
			return getUserByCredentials(username, password).isAdmin();
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
		for (BotUser user : users) {
			if ((user.getName().equalsIgnoreCase(username) || (user.getEmail() != null && user.getEmail().equalsIgnoreCase(username))) && user.getPasssword().equals(password))
				return user;
		}
		return null;
	}
}
