package com.github.black0nion.blackonionbot.commands;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.CommandVisibility;
import com.github.black0nion.blackonionbot.misc.Progress;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public interface Command {
	default CommandVisibility getVisisbility() {
		return CommandVisibility.SHOWN;
	}
	
	String[] getCommand();

	void execute(String[] args, final GuildMessageReceivedEvent e, final BlackMessage message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel);
	
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
	
	default Permission[] getRequiredBotPermissions() {
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