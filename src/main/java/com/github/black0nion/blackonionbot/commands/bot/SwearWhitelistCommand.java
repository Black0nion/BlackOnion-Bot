package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class SwearWhitelistCommand extends SlashCommand {

	public SwearWhitelistCommand() {
		this.setCommand("swearwhitelist", "sw", "antiswearwhitelist", "asw").setSyntax("<add | remove> <@role | #channel | Permission Name>").setRequiredPermissions(Permission.ADMINISTRATOR);
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
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
				final List<String> temp = new ArrayList<>(newWhitelist);
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