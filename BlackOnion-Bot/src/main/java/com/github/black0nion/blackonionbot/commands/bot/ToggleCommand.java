package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.ToggleAPI;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ToggleCommand extends Command {
	
	public ToggleCommand() {
		this.setCommand("toggle")
			.setSyntax("<command> <enabled / true / on | disabled / false / off>")
			.setRequiredPermissions(Permission.ADMINISTRATOR)
			.setRequiredArgumentCount(2)
			.setDashboardCommand(false)
			.notToggleable();
	}

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		Command command = CommandBase.commands.get(args[1]);
		if (command == null || command.isVisible()) {
			cmde.error("wrongargument", "commandnotfound");
			return;
		}
		boolean activated;
		final String activatedUnparsed = args[2];
		if (Utils.equalsOneIgnoreCase(activatedUnparsed, "enabled", "true", "on")) {
			activated = true;
		} else if (Utils.equalsOneIgnoreCase(activatedUnparsed, "disabled", "false", "off")) {
			activated = false;
		} else {
			cmde.sendPleaseUse();
			return;
		}
		
		if (ToggleAPI.setActivated(guild.getIdLong(), command, activated)) {
			final String commandName = command.getCommand()[0].toUpperCase();
			cmde.success("commandtoggled", "commandisnow", new Placeholder("command", commandName), new Placeholder("status", cmde.getTranslation(activated ? "on" : "off")));
			return;
		} else {
			cmde.error("commandcantbetoggled", "thiscommandcantbetoggled");
			return;
		}
	}
}