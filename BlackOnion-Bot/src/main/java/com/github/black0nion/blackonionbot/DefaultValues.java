package com.github.black0nion.blackonionbot;

import java.util.ArrayList;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.enums.RunMode;
import com.github.black0nion.blackonionbot.utils.ValueManager;

public class DefaultValues {
	public static void init() {
		setDefault("runMode", "production");
		
		setDefault("prefix", "?");
		
		setDefault("notifyUsers", new ArrayList<String>());
		
		setDefault("status", "dnd");

		setDefault("activityType", "playing");
		
		setDefault("activity", "spiele lul");

		setDefault("bdayDelay", 1000);
		
		if (ValueManager.getInt("lines") == 0)
			ValueManager.save("lines", 1337);
		
		if (ValueManager.getInt("files") == 0)
			ValueManager.save("files", 69);
		
		Bot.runMode = RunMode.valueOf(ValueManager.getString("runMode").toUpperCase());
		
		BotInformation.defaultPrefix = ValueManager.getString("prefix");
		
		Bot.notifyStatusUsers = new ArrayList<String>(ValueManager.getArrayAsList("notifyUsers"));
	}
	
	private static void setDefault(String key, Object value) {
		if (check(key))
			ValueManager.save(key, value);
	}
	
	private static boolean check(String key) {
		if (ValueManager.get(key) == null) 
			return true;
		return false;
	}
}
