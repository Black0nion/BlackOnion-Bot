package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.CommandVisibility;
import com.github.black0nion.blackonionbot.systems.ToggleAPI;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ToggleCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "toggle" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		Command command = CommandBase.commands.get(args[1]);
		if (command == null || command.getVisisbility() == CommandVisibility.HIDDEN) {
			channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("wrongargument", "commandnotfound", false).build()).queue();;
			return;
		}
		boolean activated;
		final String activatedUnparsed = args[2];
		if (Utils.equalsOneIgnoreCase(activatedUnparsed, "enabled", "true", "on")) {
			activated = true;
		} else if (Utils.equalsOneIgnoreCase(activatedUnparsed, "disabled", "false", "off")) {
			activated = false;
		} else {
			channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("wrongargument", Utils.getPleaseUse(guild, author, this), false).build()).queue();
			return;
		}
		
		if (ToggleAPI.setActivated(guild.getId(), command, activated)) {
			final String commandName = command.getCommand()[0].toUpperCase();
			channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild).addField(LanguageSystem.getTranslatedString("commandtoggled", author, guild).replace("%command%", commandName), LanguageSystem.getTranslatedString("commandisnow", author, guild).replace("%command%", commandName).replace("%status%", LanguageSystem.getTranslatedString(activated ? "on" : "off", author, guild)), false).build()).queue();
			return;
		} else {
			channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("commandcantbetoggled", "thiscommandcantbetoggled", false).build()).queue();
			return;
		}
	}

	@Override
	public Category getCategory() {
		return Category.BOT;
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
		return "<command> <enabled | true | on / disabled | false | off>";
	}
	
	@Override
	public boolean isDashboardCommand() {
		return false;
	}
	
	@Override
	public boolean isToggleable() {
		return false;
	}
}
