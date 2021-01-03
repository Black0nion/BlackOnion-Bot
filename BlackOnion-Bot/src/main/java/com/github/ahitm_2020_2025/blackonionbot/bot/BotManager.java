package com.github.ahitm_2020_2025.blackonionbot.bot;

import com.github.ahitm_2020_2025.blackonionbot.BotInformation;
import com.github.ahitm_2020_2025.blackonionbot.utils.ValueManager;

public class BotManager {
	public static boolean updatePrefix(String newPrefix) {
		BotInformation.prefix = newPrefix;
		ValueManager.save("prefix", newPrefix);
		return true;
	}
}
