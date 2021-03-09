package com.github.black0nion.blackonionbot.commands.moderation;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.enums.CommandVisibility;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class RenameCommand implements Command {

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member sentmember, User author, Guild guild, MessageChannel channel) {
		message.delete().queue();
		final Member mem = guild.getMemberById(args[1]);
		final List<Member> members = e.getGuild().retrieveMembersByPrefix(args[1], 99).get();
		if (members.size() != 0 || mem != null) {
			Member member;
			if (members.size() != 0) member = members.get(0);
			if (mem != null) member = mem;
			else return;
			String nickname = "";
			for (int i = 2; i < args.length; i++) {
				nickname += args[i] + " ";
			}
			nickname.trim();
			member.modifyNickname(String.join(" ", nickname)).queue();
			return;
		} else {
			channel.sendMessage("Doesn't work m8").submit().join().delete().queueAfter(3, TimeUnit.SECONDS);
			return;
		}
	}
	
	@Override
	public String getSyntax() {
		return "<old name> <new name>";
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
