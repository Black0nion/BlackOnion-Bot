package com.github.ahitm_2020_2025.blackonionbot;

import org.jetbrains.annotations.NotNull;

import com.github.ahitm_2020_2025.blackonionbot.enums.Category;
import com.github.ahitm_2020_2025.blackonionbot.enums.CommandVisisbility;
import com.github.ahitm_2020_2025.blackonionbot.enums.Progress;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface Command {
	default CommandVisisbility getVisisbility() {
		return CommandVisisbility.SHOWN;
	}
	
	String[] getCommand();

	void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, MessageChannel channel);
	
	@NotNull String getDescription();
	
	default String getSyntax() {
		return "";
	}
	
	default Category getCategory() {
		return Category.ALL;
	}
	
	default Progress getProgress() {
		return Progress.DONE;
	}
}
