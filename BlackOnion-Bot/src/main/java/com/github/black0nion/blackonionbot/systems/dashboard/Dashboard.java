package com.github.black0nion.blackonionbot.systems.dashboard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackHashMap;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.systems.dashboard.values.DashboardValue;
import com.github.black0nion.blackonionbot.systems.dashboard.values.types.DashboardBoolean;
import com.github.black0nion.blackonionbot.systems.dashboard.values.types.DashboardMultipleChoice;
import com.github.black0nion.blackonionbot.systems.dashboard.values.types.DashboardString;
import com.github.black0nion.blackonionbot.utils.Utils;

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
						 new DashboardString("antiSwearString", "AntiString", "moin"),
						 new DashboardBoolean("test", "moin meister", false));
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
	
	public static boolean tryUpdateValue(String message) {
		// should be only "updatevalue"
		final String[] input = message.split(" ");
		// syntax: guildid databasekey newvalue
		final String[] args = Utils.removeFirstArg(input);
		final String guildid = args[0];
		if (args.length < 3 || !Utils.isLong(guildid)) return false;
		DashboardValue value = getDashboardValueFromKey(input[2]);
		if (value == null) return false;
		return value.save(args[1], args[2], BlackGuild.from(Long.parseLong(guildid)));
	}
}