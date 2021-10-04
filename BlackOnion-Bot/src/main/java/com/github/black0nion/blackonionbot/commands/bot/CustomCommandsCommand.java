/**
 *
 */
package com.github.black0nion.blackonionbot.commands.bot;

import java.util.stream.Collectors;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.systems.CustomCommand;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * @author _SIM_
 *
 */
public class CustomCommandsCommand extends SlashCommand {

    public CustomCommandsCommand() {
	this.setData(new CommandData("customcommand", "Manage custom commands for your server")
		.addSubcommands(new SubcommandData("list", "Shows a list of all CustomCommands of this Server"),
			new SubcommandData("create", "Create a new subcommand").addOption(OptionType.STRING, "name", "Name of the new Command"),
			new SubcommandData("delete", "Delete a existing subcommand").addOption(OptionType.STRING, "name", "Name of the existing Command to get deleted")))
	.setRequiredPermissions(Permission.ADMINISTRATOR);

    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final String mode = e.getSubcommandName();
	if (mode.equalsIgnoreCase("list")) {
	    cmde.success("customcommandslist", guild.getCustomCommands().values().stream().map(val -> "- `" + val.getCommand() + "`").collect(Collectors.joining("\n")));
	} else if (mode.equalsIgnoreCase("create") || mode.equalsIgnoreCase("setup")) {
	    final String commandName = e.getOptionsByType(OptionType.STRING).get(0).getAsString();
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
	    final String commandName = e.getOptionsByType(OptionType.STRING).get(0).getAsString();

	    if (guild.getCustomCommands().containsKey(commandName)) {
		this.askForDelete(commandName, cmde);
	    } else {
		cmde.error("notfound", "commandnotfound");
	    }
	} else {
	    cmde.sendPleaseUse();
	}
    }

    private final void askForDelete(final String command, final SlashCommandExecutedEvent cmde) {
	cmde.reply(cmde.success().addField("areyousure", "@blaumeise was soll hier stehen?", false), msg -> {
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

    private final void askForType(final String command, final SlashCommandExecutedEvent cmde) {
	cmde.reply(cmde.success().addField("inputtype", "validtypes", false).setDescription(cmde.getTranslation("leavetutorial")).setAuthor(cmde.getTranslation("customcommandsetup", new Placeholder("cmd", command)), cmde.getJda().getSelfUser().getAvatarUrl()), msg -> {
	    CommandBase.waiter.waitForEvent(GuildMessageReceivedEvent.class, e -> e.getChannel().getIdLong() == cmde.getChannel().getIdLong() && e.getAuthor().getIdLong() == cmde.getUser().getIdLong(), e -> {
		final String contentRaw = e.getMessage().getContentRaw();
		if (contentRaw.startsWith(cmde.getGuild().getPrefix()) || Utils.equalsOneIgnoreCase(contentRaw, "exit", "leave", "cancel")) {
		    cmde.error("aborting", "byeeee");
		    return;
		}

		if (contentRaw.equalsIgnoreCase("raw") || contentRaw.equalsIgnoreCase("message")) {
		    askForRaw(command, cmde);
		} else if (contentRaw.equalsIgnoreCase("embed")) {
		    // TODO: add embed
		} else {
		    this.askForType(command, cmde);
		}
	    });
	});
    }

    private static final void askForRaw(final String command, final SlashCommandExecutedEvent cmde) {
	cmde.reply(cmde.success().addField("messagetosend", "inputmessage", false).setDescription(cmde.getTranslation("leavetutorial")).setAuthor(cmde.getTranslation("customcommandsetup", new Placeholder("cmd", command)), cmde.getJda().getSelfUser().getAvatarUrl()), msg -> {
	    CommandBase.waiter.waitForEvent(GuildMessageReceivedEvent.class, e -> e.getChannel().getIdLong() == cmde.getChannel().getIdLong() && e.getAuthor().getIdLong() == cmde.getUser().getIdLong(), e -> {
		final String contentRaw = e.getMessage().getContentRaw();
		if (contentRaw.startsWith(cmde.getGuild().getPrefix()) || Utils.equalsOneIgnoreCase(contentRaw, "exit", "leave", "cancel")) {
		    cmde.error("aborting", "byeeee");
		    return;
		}

		final CustomCommand customCommand = new CustomCommand(cmde.getGuild(), command, contentRaw);
		askForReply(command, cmde, customCommand);
	    });
	});
    }

    private static final void askForReply(final String command, final SlashCommandExecutedEvent cmde, final CustomCommand customCommand) {
	cmde.reply(cmde.success().addField("shouldreply", "shouldanswer", false).setDescription(cmde.getTranslation("leavetutorial")).setAuthor(cmde.getTranslation("customcommandsetup", new Placeholder("cmd", command)), cmde.getJda().getSelfUser().getAvatarUrl()), msg -> {
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
		    askForReply(command, cmde, customCommand);
		    return;
		}

		customCommand.setReply(reply);
		cmde.getGuild().addCustomCommand(customCommand);
		cmde.success("commandadded", "executetutorial", new Placeholder("%cmd%", cmde.getGuild().getPrefix() + customCommand.getCommand()));
	    });
	});
    }
}