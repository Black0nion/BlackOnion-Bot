/**
 *
 */
package com.github.black0nion.blackonionbot.commands.bot;

import java.util.stream.Collectors;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.CustomCommand;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * @author _SIM_
 *
 */
public class CustomCommandsCommand extends Command {

    public CustomCommandsCommand() {
	this.setCommand("customcommand", "cc", "ccs").setSyntax("<list | create | delete> [command (required for create and delete)]").setRequiredArgumentCount(1).setRequiredPermissions(Permission.ADMINISTRATOR);

    }

    @Override
    public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final String mode = args[1];
	if (mode.equalsIgnoreCase("list")) {
	    cmde.success("customcommandslist", guild.getCustomCommands().values().stream().map(val -> "- `" + val.getCommand() + "`").collect(Collectors.joining("\n")));
	} else if (mode.equalsIgnoreCase("create") || mode.equalsIgnoreCase("setup")) {
	    final String commandName = args[2].toLowerCase();
	    final int maxCount = guild.getGuildType().getMaxCustomCommands();

	    if (guild.getCustomCommands().size() >= maxCount) {
		cmde.error("toomanycustomcommands", "maxcustomcommands", new Placeholder("count", maxCount));
		return;
	    }

	    if (CommandBase.commands.containsKey(commandName) || guild.getCustomCommands().containsKey(commandName)) {
		cmde.error("alreadyexisting", "commandexisting");
		return;
	    }

	    this.askForType(commandName, cmde);
	} else if (mode.equalsIgnoreCase("delete")) {
	    final String commandName = args[2].toLowerCase();

	    if (guild.getCustomCommands().containsKey(commandName)) {
		this.askForDelete(commandName, cmde);
	    } else {
		cmde.error("notfound", "commandnotfound");
	    }
	} else {
	    cmde.sendPleaseUse();
	}
    }

    private final void askForDelete(final String command, final CommandEvent cmde) {
	cmde.getMessage().replyEmbeds(cmde.success().addField("areyousure", "@blaumeise was soll hier stehen?", false).build()).queue(msg -> {
	    CommandBase.waiter.waitForEvent(GuildMessageReceivedEvent.class, e -> e.getChannel().getIdLong() == cmde.getChannel().getIdLong() && e.getAuthor().getIdLong() == cmde.getUser().getIdLong(), e -> {
		final String contentRaw = e.getMessage().getContentRaw();

		if (contentRaw.equalsIgnoreCase("true")) {
		    cmde.getGuild().deleteCustomCommand(command);
		    cmde.success("entrydeleted", "commanddeleted", new Placeholder("cmd", command));
		} else {
		    cmde.error("abort", "nothingdeleted");
		}
	    });
	});
    }

    private final void askForType(final String command, final CommandEvent cmde) {
	cmde.getMessage().replyEmbeds(cmde.success().addField("inputtype", "validtypes", false).setDescription(cmde.getTranslation("leavetutorial")).setAuthor(cmde.getTranslation("customcommandsetup", new Placeholder("cmd", command)), cmde.getJda().getSelfUser().getAvatarUrl()).build()).queue(msg -> {
	    CommandBase.waiter.waitForEvent(GuildMessageReceivedEvent.class, e -> e.getChannel().getIdLong() == cmde.getChannel().getIdLong() && e.getAuthor().getIdLong() == cmde.getUser().getIdLong(), e -> {
		final String contentRaw = e.getMessage().getContentRaw();
		if (contentRaw.startsWith(cmde.getGuild().getPrefix()) || Utils.equalsOneIgnoreCase(contentRaw, "exit", "leave", "cancel")) {
		    cmde.error("aborting", "byeeee");
		    return;
		}

		if (contentRaw.equalsIgnoreCase("raw") || contentRaw.equalsIgnoreCase("message")) {
		    askForRaw(command, new CommandEvent(e, cmde.getGuild(), e.getMessage(), cmde.getMember(), cmde.getUser()));
		} else if (contentRaw.equalsIgnoreCase("embed")) {
		    // TODO: add embed
		} else {
		    this.askForType(command, new CommandEvent(e, cmde.getGuild(), e.getMessage(), cmde.getMember(), cmde.getUser()));
		}
	    });
	});
    }

    private static final void askForRaw(final String command, final CommandEvent cmde) {
	cmde.getMessage().replyEmbeds(cmde.success().addField("messagetosend", "inputmessage", false).setDescription(cmde.getTranslation("leavetutorial")).setAuthor(cmde.getTranslation("customcommandsetup", new Placeholder("cmd", command)), cmde.getJda().getSelfUser().getAvatarUrl()).build()).queue(msg -> {
	    CommandBase.waiter.waitForEvent(GuildMessageReceivedEvent.class, e -> e.getChannel().getIdLong() == cmde.getChannel().getIdLong() && e.getAuthor().getIdLong() == cmde.getUser().getIdLong(), e -> {
		final String contentRaw = e.getMessage().getContentRaw();
		if (contentRaw.startsWith(cmde.getGuild().getPrefix()) || Utils.equalsOneIgnoreCase(contentRaw, "exit", "leave", "cancel")) {
		    cmde.error("aborting", "byeeee");
		    return;
		}

		final CustomCommand customCommand = new CustomCommand(cmde.getGuild(), command, contentRaw);
		askForReply(command, new CommandEvent(e, cmde.getGuild(), e.getMessage(), cmde.getMember(), cmde.getUser()), customCommand);
	    });
	});
    }

    private static final void askForReply(final String command, final CommandEvent cmde, final CustomCommand customCommand) {
	cmde.getMessage().replyEmbeds(cmde.success().addField("shouldreply", "shouldanswer", false).setDescription(cmde.getTranslation("leavetutorial")).setAuthor(cmde.getTranslation("customcommandsetup", new Placeholder("cmd", command)), cmde.getJda().getSelfUser().getAvatarUrl()).build()).queue(msg -> {
	    CommandBase.waiter.waitForEvent(GuildMessageReceivedEvent.class, e -> e.getChannel().getIdLong() == cmde.getChannel().getIdLong() && e.getAuthor().getIdLong() == cmde.getUser().getIdLong(), e -> {
		final String contentRaw = e.getMessage().getContentRaw();

		if (contentRaw.startsWith(cmde.getGuild().getPrefix()) || Utils.equalsOneIgnoreCase(contentRaw, "exit", "leave", "cancel")) {
		    cmde.error("aborting", "byeeee");
		    return;
		}

		boolean reply;
		if (contentRaw.equalsIgnoreCase("true")) {
		    reply = true;
		} else if (contentRaw.equalsIgnoreCase("false")) {
		    reply = false;
		} else {
		    askForReply(command, new CommandEvent(e, cmde.getGuild(), e.getMessage(), cmde.getMember(), cmde.getUser()), customCommand);
		    return;
		}

		customCommand.setReply(reply);
		cmde.getGuild().addCustomCommand(customCommand);
		cmde.success("commandadded", "executetutorial", new Placeholder("%cmd%", cmde.getGuild().getPrefix() + customCommand.getCommand()));
	    });
	});
    }
}