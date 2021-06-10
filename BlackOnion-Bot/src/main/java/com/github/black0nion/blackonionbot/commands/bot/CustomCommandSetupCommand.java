/**
 *
 */
package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.CustomCommand;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * @author _SIM_
 *
 */
public class CustomCommandSetupCommand extends Command {

    public CustomCommandSetupCommand() {
	this.setCommand("customcommandsetup", "creatcustomcommand", "ccc", "ccs").setSyntax("<command>").setRequiredArgumentCount(1).setRequiredPermissions(Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final BlackMessage message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	if (CommandBase.commands.containsKey(args[1].toLowerCase()) || guild.getCustomCommands().containsKey(args[1].toLowerCase())) {
	    cmde.error("alreadyexisting", "commandexisting");
	    return;
	}

	askForType(args[1].toLowerCase(), cmde);
    }

    private final void askForType(final String command, final CommandEvent cmde) {
	cmde.success("inputtype", "validtypes", msg -> {
	    CommandBase.waiter.waitForEvent(GuildMessageReceivedEvent.class, e -> e.getChannel().getIdLong() == cmde.getChannel().getIdLong() && e.getAuthor().getIdLong() == cmde.getUser().getIdLong(), e -> {
		final String contentRaw = e.getMessage().getContentRaw();
		if (contentRaw.startsWith(cmde.getGuild().getPrefix()) || Utils.equalsOneIgnoreCase(contentRaw, "exit", "leave", "cancel")) {
		    cmde.error("aborting", "byeeee");
		    return;
		}
		if (contentRaw.equalsIgnoreCase("raw") || contentRaw.equalsIgnoreCase("message")) {
		    askForRaw(command, new CommandEvent(e, cmde.getGuild(), BlackMessage.from(e.getMessage()), cmde.getMember(), cmde.getUser()));
		} else if (contentRaw.equalsIgnoreCase("embed")) {

		} else {
		    askForType(command, new CommandEvent(e, cmde.getGuild(), BlackMessage.from(e.getMessage()), cmde.getMember(), cmde.getUser()));
		}
	    });
	});
    }

    private static final void askForRaw(final String command, final CommandEvent cmde) {
	cmde.success("messagetosend", "inputmessage", msg -> {
	    CommandBase.waiter.waitForEvent(GuildMessageReceivedEvent.class, e -> e.getChannel().getIdLong() == cmde.getChannel().getIdLong() && e.getAuthor().getIdLong() == cmde.getUser().getIdLong(), e -> {
		final String contentRaw = e.getMessage().getContentRaw();
		if (contentRaw.startsWith(cmde.getGuild().getPrefix()) || Utils.equalsOneIgnoreCase(contentRaw, "exit", "leave", "cancel")) {
		    cmde.error("aborting", "byeeee");
		    return;
		}
		final CustomCommand customCommand = new CustomCommand(cmde.getGuild(), command, contentRaw);
		cmde.getGuild().addCustomCommand(customCommand);
		cmde.success("commandadded", "executetutorial", new Placeholder("%cmd%", cmde.getGuild().getPrefix() + customCommand.getCommand()));
	    });
	});
    }
}