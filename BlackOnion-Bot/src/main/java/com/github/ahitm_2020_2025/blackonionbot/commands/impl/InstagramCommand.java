package com.github.ahitm_2020_2025.blackonionbot.commands.impl;

import com.github.ahitm_2020_2025.blackonionbot.Command;
import com.github.ahitm_2020_2025.blackonionbot.enums.Progress;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InstagramCommand implements Command {

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, MessageChannel channel) {
		channel.sendMessage("Bitte schrei Insta an weil die nur ne scheißapi haben xD").queue();
	}

	@Override
	public String getDescription() {
		return "Gibt Infos über einen Instagram User aus";
	}
	
	@Override
	public Progress getProgress() {
		return Progress.PLANNED;
	}

	@Override
	public String[] getCommand() {
		return new String[]{"instagram", "insta"};
	}
}
