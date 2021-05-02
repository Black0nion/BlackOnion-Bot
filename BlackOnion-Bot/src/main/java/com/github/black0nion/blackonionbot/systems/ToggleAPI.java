package com.github.black0nion.blackonionbot.systems;

import java.util.HashMap;
import java.util.List;

import com.github.black0nion.blackonionbot.commands.Command;

public class ToggleAPI {
	private static HashMap<String, List<Command>> disabledCommands = new HashMap<>();
	
	public static void setActivated(String guildId, String command, boolean activated) {
		
	}
	
	public static boolean isActivated(String guildId, String command) {
		
		return false;
	}
}
