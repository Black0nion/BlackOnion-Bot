package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class JoinLeaveMessageCommand implements Command {
	
	@Override
	public String[] getCommand() {
		return new String[] { "joinleavemessage", "jlm" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		final String newMessage = String.join(" ", Utils.subArray(args, 2));
		if (args[1].equalsIgnoreCase("join")) {
			guild.setJoinMessage(newMessage);
			message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField("setjoinmessage", LanguageSystem.getTranslation("joinmessagesetto", author, guild).replace("%msg%", "``" + newMessage + "``"), false).build()).queue();
		} else if (args[1].equalsIgnoreCase("leave")) {
			guild.setLeaveMessage(newMessage);
			message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField("setleavemessage", LanguageSystem.getTranslation("leavemessagesetto", author, guild).replace("%msg%", "``" + newMessage + "``"), false).build()).queue();
		} else {
			message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("wrongargument", Utils.getPleaseUse(guild, author, this), false).build()).queue();
		}
	}
	
	@Override
	public Permission[] getRequiredPermissions() {
		return new Permission[] { Permission.ADMINISTRATOR };
	}

	@Override
	public int getRequiredArgumentCount() {
		return 2;
	}
	
	@Override
	public String getSyntax() {
		return "<join | leave> <message>";
	}
	
	@Override
	public Category getCategory() {
		return Category.BOT;
	}
}