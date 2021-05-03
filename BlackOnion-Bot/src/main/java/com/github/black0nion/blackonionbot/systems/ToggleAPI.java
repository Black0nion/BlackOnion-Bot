package com.github.black0nion.blackonionbot.systems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;

public class ToggleAPI {
	public static HashMap<String, List<Command>> disabledCommands = new HashMap<>();
	
	/**
	 * @param guildId
	 * @param command
	 * @param activated
	 * @return if it worked
	 */
	public static boolean setActivated(String guildId, String command, boolean activated) {
		if (CommandBase.commands.containsKey(command)) {
			Command cmd = CommandBase.commands.get(command);
			if (!cmd.isToggleable()) return false;
			List<Command> disabledCommandsForGuild;
			if (disabledCommands.containsKey(guildId)) {
				disabledCommandsForGuild = disabledCommands.get(guildId).stream().collect(Collectors.toList());
			} else {
				disabledCommandsForGuild = new ArrayList<>();
			}
			if (activated) disabledCommandsForGuild.remove(cmd);
			else if (!disabledCommandsForGuild.contains(cmd)) disabledCommandsForGuild.add(cmd);
			disabledCommands.put(guildId, disabledCommandsForGuild);
			return true;
		}
		return false;
	}
	
	public static boolean isActivated(String guildId, String command) {
		return false;
	}
}
