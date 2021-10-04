package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

// TODO: add SlashCommand support
public class ToggleCommand extends SlashCommand {

    public ToggleCommand() {
	this.setData(new CommandData("toggle", "Toggles a specific command")
		.addOption(OptionType.STRING, "command", "The command to toggle", true)
		.addOption(OptionType.BOOLEAN, "enabled", "Should the command be enabled or disabled?"))
	.setRequiredPermissions(Permission.ADMINISTRATOR).notToggleable();
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final Command command = CommandBase.commands.get(e.getOptionsByType(OptionType.STRING).get(0).getAsString());
	if (command == null || !command.isVisible(author)) {
	    cmde.error("wrongargument", "commandnotfound");
	    return;
	}

	if (e.getOptionsByType(OptionType.BOOLEAN).isEmpty()) {
	    if (!command.isToggleable()) {
		cmde.error("commandcantbetoggled", "thiscommandcantbetoggled");
		return;
	    } else {
		cmde.success("commandstatus", "commandisnow", new Placeholder("command", command.getCommand()[0].toUpperCase()), new Placeholder("status", cmde.getTranslation(guild.isCommandActivated(command) ? "on" : "off")));
		return;
	    }
	} else {
	    final boolean activated = e.getOptionsByType(OptionType.BOOLEAN).get(0).getAsBoolean();

	    if (guild.setCommandActivated(command, activated)) {
		final String commandName = command.getCommand()[0].toUpperCase();
		cmde.success("commandtoggled", "commandisnow", new Placeholder("command", commandName), new Placeholder("status", cmde.getTranslation(activated ? "on" : "off")));
		return;
	    } else {
		cmde.error("commandcantbetoggled", "thiscommandcantbetoggled");
		return;
	    }
	}
    }
}