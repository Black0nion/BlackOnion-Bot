package com.github.black0nion.blackonionbot.bot;

import com.github.black0nion.blackonionbot.utils.ValueManager;

public class BotManager {
	public static boolean updatePrefix(String newPrefix) {
		BotInformation.prefix = newPrefix;
		ValueManager.save("prefix", newPrefix);
		return true;
	}
}
