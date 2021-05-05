package com.github.black0nion.blackonionbot.systems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.systems.guildmanager.GuildManager;

public class ToggleAPI {
	public static HashMap<String, List<Command>> disabledCommands = new HashMap<>();
	
	public static void init() {
		for (Document doc : GuildManager.collection.find()) {
			if (doc.containsKey("disabledCommands")) {
				List<String> disabledCommandsString = doc.getList("disabledCommands", String.class);
				List<Command> disabledCommandsForGuild = new ArrayList<>();
				for (String s : disabledCommandsString) {
					if (CommandBase.commands.containsKey(s)) disabledCommandsForGuild.add(CommandBase.commands.get(s.toLowerCase()));
				}
				disabledCommands.put(doc.getString("guildid"), disabledCommandsForGuild);
			}
		}
	}
	
	/**
	 * @param guildId
	 * @param command
	 * @param activated
	 * @return if it worked
	 */
	public static boolean setActivated(String guildId, String command, boolean activated) {
		if (CommandBase.commands.containsKey(command.toLowerCase()))
			return setActivated(guildId, CommandBase.commands.get(command.toLowerCase()), activated);
		return false;
	}
	
	/**
	 * @param guildId
	 * @param command
	 * @param activated
	 * @return if it worked
	 */
	public static boolean setActivated(String guildId, Command cmd, boolean activated) {
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
		saveToMongo();
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
		if (disabledCommands.containsKey(guildId) && disabledCommands.get(guildId).contains(command)) return false;
		return true;
	}
	
	private static void saveToMongo() {
		Bot.executor.submit(() -> {
			disabledCommands.forEach((guild, commands) -> {
				GuildManager.saveList(guild, "disabledCommands", commands.stream().map(cmd -> { return cmd.getCommand()[0]; }).collect(Collectors.toList()));
			});
		});
	}
}
