package com.github.black0nion.blackonionbot.systems.dashboard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;

public class Dashboard {
	/**
	 * Those are the possible settings
	 * HashMap<Command the value applys to, HashMap<Map.Entry<Database Key, pretty name>, Class the value is in>>
	 */
	private static HashMap<Command, List<DashboardValue>> values = new HashMap<>();
	
	public static void init() {
		values.put(CommandBase.commands.get("antiswear"), Arrays.asList(new DashboardValue("antiSwear", "AntiSwear", DashboardValueType.BOOLEAN)));
		System.out.println(values);
	}
	
	public static boolean hasValues(Command cmd) {
		return values.containsKey(cmd);
	}
	
	public static List<DashboardValue> getValues(Command cmd) {
		return values.get(cmd);
	}
}