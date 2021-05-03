package com.github.black0nion.blackonionbot.commands;

import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.CommandVisibility;
import com.github.black0nion.blackonionbot.misc.Progress;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public interface Command {
	default CommandVisibility getVisisbility() {
		return CommandVisibility.SHOWN;
	}
	
	String[] getCommand();

	void execute(String[] args, final GuildMessageReceivedEvent e, final Message message, final Member member, final User author, final Guild guild, final MessageChannel channel);
	
	default String getSyntax() {
		return "";
	}
	
	default Category getCategory() {
		return Category.OTHER;
	}
	
	default Progress getProgress() {
		return Progress.DONE;
	}
	
	default int getRequiredArgumentCount() {
		return 0;
	}
	
	default Permission[] getRequiredPermissions() {
		return null;
	}
	
	default boolean requiresBotAdmin() {
		return false;
	}
	
	default boolean isToggleable() {
		return true;
	}
	
	default boolean isDashboardCommand() {
		return true;
	}
}
