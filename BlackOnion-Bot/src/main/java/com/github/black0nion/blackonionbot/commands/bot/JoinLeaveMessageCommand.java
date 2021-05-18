package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.guildmanager.GuildManager;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class JoinLeaveMessageCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "joinleavemessage", "jlm" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, TextChannel channel) {
		final String newMessage = String.join(" ", Utils.subArray(args, 2));
		if (args[1].equalsIgnoreCase("join")) {
			GuildManager.save(guild, "joinmessage", newMessage);
			channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild).addField("setjoinmessage", LanguageSystem.getTranslatedString("joinmessagesetto", author, guild).replace("%msg%", "``" + newMessage + "``"), false).build()).queue();
		} else if (args[1].equalsIgnoreCase("leave")) {
			GuildManager.save(guild, "leavemessage", newMessage);
			channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild).addField("setleavemessage", LanguageSystem.getTranslatedString("leavemessagesetto", author, guild).replace("%msg%", "``" + newMessage + "``"), false).build()).queue();
		} else {
			channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("wrongargument", Utils.getPleaseUse(guild, author, this), false).build()).queue();
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
