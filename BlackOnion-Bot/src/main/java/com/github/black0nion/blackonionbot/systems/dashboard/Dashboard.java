package com.github.black0nion.blackonionbot.systems.dashboard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.BlackHashMap;

public class Dashboard {
	/**
	 * Those are the possible settings
	 * HashMap<Command the value applys to, HashMap<Map.Entry<Database Key, pretty name>, Class the value is in>>
	 */
	private static HashMap<Command, List<DashboardValue>> values = new HashMap<>();
	
	public static void init() {
		values.put(CommandBase.commands.get("antiswear"), Arrays.asList(new DashboardValue("antiSwear", "AntiSwear", DashboardValueType.MULTIPLE_CHOICE, new BlackHashMap<String, String>().putAndGetSelf("delete", "Delete").putAndGetSelf("resend", "Resend").putAndGetSelf("off", "Off"))));
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
	
	public static boolean tryParse(DashboardValue dashboardValue, Object value) {
		DashboardValueType type = dashboardValue.getType();
		if (type == DashboardValueType.MULTIPLE_CHOICE) {
			if (dashboardValue.getMultipleChoice().containsKey(value))
				System.out.println("yesssir");
		} else if (type == DashboardValueType.BOOLEAN) {
			System.out.println(value);
		} else if (type == DashboardValueType.STRING) {
			
		} else if (type == DashboardValueType.BOOLEAN) {
			
		}
		return false;
	}
}