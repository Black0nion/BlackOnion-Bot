package com.github.black0nion.blackonionbot.commands.misc;

import com.github.black0nion.blackonionbot.enums.CommandVisibility;
import com.github.black0nion.blackonionbot.oldcommands.Command;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class TestCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] {"test"};
	}

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		channel.sendMessage("test").queue();
	}
	
	@Override
	public CommandVisibility getVisisbility() {
		return CommandVisibility.HIDDEN;
	}
	
	@Override
	public Permission[] getRequiredPermissions() {
		return new Permission[] {Permission.MESSAGE_MANAGE};
	}

}
