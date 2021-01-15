package com.github.black0nion.blackonionbot.oldcommands;

import com.github.black0nion.blackonionbot.enums.Category;
import com.github.black0nion.blackonionbot.enums.CommandVisibility;
import com.github.black0nion.blackonionbot.enums.Progress;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface Command {
	default CommandVisibility getVisisbility() {
		return CommandVisibility.SHOWN;
	}
	
	String[] getCommand();

	void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel);
	
	default String getSyntax() {
		return "";
	}
	
	default Category getCategory() {
		return Category.ALL;
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
	
	default boolean dmCommand() {
		return false;
	}
}
