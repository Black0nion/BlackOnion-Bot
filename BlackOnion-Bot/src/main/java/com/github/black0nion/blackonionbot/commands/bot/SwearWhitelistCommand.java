package com.github.black0nion.blackonionbot.commands.bot;

import java.util.ArrayList;
import java.util.List;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

public class SwearWhitelistCommand extends SlashCommand {

    public SwearWhitelistCommand() {
	final SubcommandData[] data = {
		new SubcommandData("role", "The entry is of type role").addOption(OptionType.ROLE, "role", "The affected role", true),
		new SubcommandData("channel", "The entry is of type channel").addOption(OptionType.CHANNEL, "channel", "The affected channel", true),
		new SubcommandData("permission", "The entry is of type permission").addOption(OptionType.STRING, "permission", "The affected permission", true)
	};
	this.setData(new CommandData("swearwhitelist", "Whitelist a role, channel or permission to bypass the Swear Filter")
		.addSubcommandGroups(new SubcommandGroupData("add", "Add something to the whitelist").addSubcommands(data),
			new SubcommandGroupData("remove", "Remove something from the whitelist").addSubcommands(data),
			new SubcommandGroupData("list", "List all existing entries on the whitelist")))
	.setRequiredPermissions(Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final String mode = e.getSubcommandGroup();
	if (mode.equals("add") || mode.equals("remove")) {
	    final List<String> mentionedStuff = new ArrayList<>();
	    if (e.getOption("role") != null) {
		mentionedStuff.add(e.getOption("role").getAsRole().getAsMention());
	    }
	    if (e.getOption("channel") != null) {
		mentionedStuff.add(e.getOption("channel").getAsGuildChannel().getAsMention());
	    }
	    if (e.getOption("permission") != null && this.isPermission(e.getOption("permission").getAsString())) {
		mentionedStuff.add(e.getOption("permission").getAsString().toUpperCase());
	    }

	    final boolean add = mode.equals("add");

	    if (mentionedStuff.size() != 0) {
		List<String> newWhitelist = guild.getAntiSwearWhitelist();
		if (newWhitelist == null) {
		    newWhitelist = new ArrayList<>();
		}
		final List<String> temp = new ArrayList<String>(newWhitelist);
		if (add) {
		    temp.retainAll(mentionedStuff);
		    newWhitelist.removeAll(temp);
		    newWhitelist.addAll(mentionedStuff);
		} else {
		    newWhitelist.removeAll(mentionedStuff);
		}
		guild.setAntiSwearWhitelist(newWhitelist);
		cmde.success("whitelistupdated", (add ? cmde.getTranslation("addedtowhitelist", new Placeholder("add", mentionedStuff.toString())) : cmde.getTranslation("removedfromwhitelist", new Placeholder("removed", mentionedStuff.toString()))));
	    }
	} else {
	    final List<String> whitelist = guild.getAntiSwearWhitelist();
	    cmde.success("antiswearwhitelist", (whitelist != null && whitelist.size() != 0 ? whitelist.toString() : "empty"));
	}
    }

    private boolean isPermission(final String asString) {
	try {
	    Permission.valueOf(asString.toUpperCase());
	    return true;
	} catch (final Exception ignored) { return false; }
    }
}