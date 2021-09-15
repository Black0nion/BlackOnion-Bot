package com.github.black0nion.blackonionbot.commands.bot;

import java.util.ArrayList;
import java.util.List;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import net.dv8tion.jda.api.entities.Message;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SwearWhitelistCommand extends Command {

    public SwearWhitelistCommand() {
	this.setCommand("swearwhitelist", "sw", "antiswearwhitelist", "asw").setSyntax("<add | remove> <@role | #channel | Permission Name>").setRequiredPermissions(Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	if (args.length >= 3 && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) {
	    final List<String> mentionedStuff = new ArrayList<>();
	    final List<Role> roles = message.getMentionedRoles();
	    final List<TextChannel> channels = message.getMentionedChannels();
	    for (int i = 2; i < args.length; i++) {
		final String input = args[i];
		final Role rl = roles.stream().filter(r -> r.getAsMention().equals(input)).findFirst().orElse(null);
		final TextChannel ch = channels.stream().filter(c -> c.getAsMention().equals(input)).findFirst().orElse(null);
		Permission perm = null;
		try {
		    perm = Permission.valueOf(input.toUpperCase());
		} catch (final Exception ignored) {
		}

		if (rl != null) {
		    mentionedStuff.add(rl.getAsMention());
		}
		if (ch != null) {
		    mentionedStuff.add(ch.getAsMention());
		}
		if (perm != null) {
		    mentionedStuff.add(perm.name());
		}
	    }

	    final boolean add = args[1].equalsIgnoreCase("add");

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
}