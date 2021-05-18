package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PrefixCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "prefix", "changeprefix", "setprefix" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		if (args[1].toCharArray().length > 10) {
			channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("toolong", "undertenchars", false).build()).queue();
			return;
		}
		BotInformation.setPrefix(guild, args[1]);
		channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild).addField(":wave:", "My Prefix is: " + BotInformation.getPrefix(guild), false).build()).queue();
	}
	
	@Override
	public Permission[] getRequiredPermissions() {
		return new Permission[] { Permission.ADMINISTRATOR };
	}
	
	@Override
	public int getRequiredArgumentCount() {
		return 1;
	}
	
	@Override
	public Category getCategory() {
		return Category.BOT;
	}
	
	@Override
	public String getSyntax() {
		return "<new prefix, no spaces, less than 10 characters>";
	}
}
