/**
 *
 */
package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * @author _SIM_
 */
public class DeleteCustomCommandCommand extends Command {

    public DeleteCustomCommandCommand() {
	this.setCommand("deletecustomcommand", "dcc", "delcustomcommand", "delcc").setSyntax("<command name>").setRequiredArgumentCount(1).setRequiredPermissions(Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final BlackMessage message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final String commandName = args[1].toLowerCase();
	if (guild.getCustomCommands().containsKey(commandName)) {
	    guild.deleteCustomCommand(commandName);
	    cmde.success("entrydeleted", "commanddeleted", new Placeholder("cmd", commandName));
	} else {
	    cmde.error("notfound", "commandnotfound");
	}
    }
}