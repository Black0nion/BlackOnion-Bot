package com.github.black0nion.blackonionbot.commands.moderation;

import java.time.Duration;
import java.util.List;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.misc.CommandVisibility;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class RenameCommand implements Command {
	
	@Override
	public String[] getCommand() {
		return new String[] { "rename", "rn" };
	}

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember sentmember, BlackUser author, BlackGuild guild, TextChannel channel) {
		if (Utils.handleRights(guild, author, e.getChannel(), Permission.MESSAGE_MANAGE, Permission.NICKNAME_MANAGE)) return;
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
			channel.sendMessage("Doesn't work m8").delay(Duration.ofSeconds(3)).flatMap(Message::delete).queue();
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
}