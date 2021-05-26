package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PrefixCommand extends Command {
	
	public PrefixCommand() {
		this.setCommand("prefix", "changeprefix", "setprefix")
			.setSyntax("<new prefix, no spaces, less than 10 characters>")
			.setRequiredPermissions(Permission.ADMINISTRATOR)
			.setRequiredArgumentCount(1);
	}

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		if (args[1].toCharArray().length > 10) {
			cmde.error("toolong", "undertenchars");
			message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("toolong", "undertenchars", false).build()).queue();
			return;
		}
		guild.setPrefix(args[1]);
		cmde.success("prefixchanged", "myprefixis", new Placeholder("prefix", guild.getPrefix()));
	}
}