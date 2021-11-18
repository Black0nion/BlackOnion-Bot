package com.github.black0nion.blackonionbot.bot;

import java.util.ArrayList;

import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.misc.RunMode;
import com.github.black0nion.blackonionbot.utils.ValueManager;

public class DefaultValues {
	
	@Reloadable("defaultvalues")
	public static void init() {
		setDefault("runMode", RunMode.DEV.name());
		
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
		
		BotInformation.DEFAULT_PREFIX = ValueManager.getString("prefix");
		
		Bot.notifyStatusUsers = new ArrayList<>(ValueManager.getArrayAsList("notifyUsers"));
	}
	
	private static void setDefault(final String key, final Object value) {
		if (check(key))
			ValueManager.save(key, value);
	}
	
	private static boolean check(final String key) {
		if (ValueManager.get(key) == null) 
			return true;
		return false;
	}
}
