package com.github.ahitm_2020_2025.blackonionbot.commands.moderation;

import java.util.concurrent.TimeUnit;

import com.github.ahitm_2020_2025.blackonionbot.enums.Category;
import com.github.ahitm_2020_2025.blackonionbot.enums.CommandVisibility;
import com.github.ahitm_2020_2025.blackonionbot.oldcommands.Command;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RenameCommand implements Command {

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member sentmember, User author,
			MessageChannel channel) {
		message.delete().queue();
		if (sentmember.hasPermission(Permission.ADMINISTRATOR)) {
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
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public CommandVisibility getVisisbility() {
		return CommandVisibility.HIDDEN;
	}
	
	@Override
	public Category getCategory() {
		return Category.MODERATION;
	}
	
	@Override
	public String[] getCommand() {
		return new String[] {"rename", "rn"};
	}
}
