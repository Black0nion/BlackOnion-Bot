package com.github.black0nion.blackonionbot.systems;

import java.util.List;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.utils.Utils;

public class ToggleAPI {
	
	/**
	 * @param guildId
	 * @param command
	 * @param activated
	 * @return if it worked
	 */
	public static boolean setActivated(String guildId, String command, boolean activated) {
		if (Utils.isLong(guildId) && CommandBase.commands.containsKey(command.toLowerCase()))
			return setActivated(Long.parseLong(guildId), CommandBase.commands.get(command.toLowerCase()), activated);
		return false;
	}
	
	/**
	 * @param guildId
	 * @param command
	 * @param activated
	 * @return if it worked
	 */
	public static boolean setActivated(long guildId, Command cmd, boolean activated) {
		if (!cmd.isToggleable()) return false;
		return setActiavted(BlackGuild.from(guildId), cmd, activated);
	}
	
	/**
	 * @param guildId
	 * @param command
	 * @param activated
	 * @return if it worked
	 */
	public static boolean setActiavted(BlackGuild guild, Command cmd, boolean activated) {
		if (!cmd.isToggleable()) return false;
		List<Command> disabledCommandsForGuild = guild.getDisabledCommands();
		if (activated) disabledCommandsForGuild.remove(cmd);
		else if (!disabledCommandsForGuild.contains(cmd)) disabledCommandsForGuild.add(cmd);
		guild.setDisabledCommands(disabledCommandsForGuild);
		return true;
	}
	
	public static boolean isActivated(String guildId, String command) {
		Command cmd = CommandBase.commands.containsKey(command.toLowerCase()) ? CommandBase.commands.get(command.toLowerCase()) : null;
		if (cmd == null) {
			try {
				throw new IllegalArgumentException("Command " + command + " not found!");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		return isActivated(guildId, cmd);
	}
	
	public static boolean isActivated(String guildId, Command command) {
		if (!Utils.isLong(guildId)) throw new NumberFormatException();
		final List<Command> disabledCommands = BlackGuild.from(Long.parseLong(guildId)).getDisabledCommands();
		if (disabledCommands != null && disabledCommands.contains(command)) return false;
		return true;
	}
}