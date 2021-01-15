package com.github.black0nion.blackonionbot.commands.moderation;

import java.util.concurrent.TimeUnit;

import com.github.black0nion.blackonionbot.enums.CommandVisibility;
import com.github.black0nion.blackonionbot.oldcommands.Command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RenameCommand implements Command {

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member sentmember, User author, Guild guild, MessageChannel channel) {
		message.delete().queue();
		e.getGuild().retrieveMembersByPrefix(args[1], 99).onSuccess(members -> {
			if (members.size() != 0) {
				Member member = members.get(0);
				String nickname = "";
				for (int i = 2; i < args.length; i++) {
					nickname += args[i] + " ";
				}
				nickname.trim();
				member.modifyNickname(String.join(" ", nickname)).queue();
				return;
			} else {
				channel.sendMessage("Doesn't work m8").complete().delete().queueAfter(3, TimeUnit.SECONDS);
				return;
			}
		});
	}
	
	@Override
	public boolean requiresBotAdmin() {
		return true;
	}

	@Override
	public CommandVisibility getVisisbility() {
		return CommandVisibility.HIDDEN;
	}
	
	@Override
	public String[] getCommand() {
		return new String[] {"rename", "rn"};
	}
}
