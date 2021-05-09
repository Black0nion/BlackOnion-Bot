package com.github.black0nion.blackonionbot.systems.dashboard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.BlackHashMap;
import com.github.black0nion.blackonionbot.systems.dashboard.values.DashboardValue;
import com.github.black0nion.blackonionbot.systems.dashboard.values.types.DashboardBoolean;
import com.github.black0nion.blackonionbot.systems.dashboard.values.types.DashboardMultipleChoice;
import com.github.black0nion.blackonionbot.systems.dashboard.values.types.DashboardString;

public class Dashboard {
	/**
	 * Those are the possible settings
	 * HashMap<Command the value applys to, HashMap<Map.Entry<Database Key, pretty name>, Class the value is in>>
	 */
	private static HashMap<Command, List<DashboardValue>> values = new HashMap<>();
	
	public static void init() {
		values.clear();
		add("antiswear", new DashboardMultipleChoice("antiSwear", "AntiSwear", new BlackHashMap<String, String>().add("delete", "Delete").add("resend", "Resend").add("off", "Off")),
						 new DashboardBoolean("antiSwearBoolean", "AntiBoolean", false),
						 new DashboardString("antiSwearString", "AntiString", "moin"));
	}
	
	private static void add(String commandName, DashboardValue... dashboardValues) {
		values.put(CommandBase.commands.get(commandName), Arrays.asList(dashboardValues));
	}
	
	public static boolean hasValues(Command cmd) {
		return values.containsKey(cmd);
	}
	
	public static List<DashboardValue> getValues(Command cmd) {
		return values.get(cmd);
	}
	
	@Nullable
	public static DashboardValue getDashboardValueFromKey(String databaseKey) {
		for (List<DashboardValue> dashboardValueList : values.values()) {
			for (DashboardValue dashboardValue : dashboardValueList) {
				if (dashboardValue.getDatabaseKey().equals(databaseKey))
					return dashboardValue;
			}
		}
		return null;
	}
}