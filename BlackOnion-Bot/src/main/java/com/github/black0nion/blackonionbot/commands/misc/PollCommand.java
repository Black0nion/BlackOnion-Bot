package com.github.black0nion.blackonionbot.commands.misc;

import java.util.Arrays;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PollCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "poll", "survey" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild).setTitle("poll").addField(String.join(" ", Arrays.copyOfRange(args, 1, args.length)), "polltutorial", false).build()).queue(msg -> {
			msg.addReaction("tick:822036832422068225").queue();
			msg.addReaction("cross:822036805117018132").queue();
		});
	}
	
	@Override
	public int getRequiredArgumentCount() {
		return 1;
	}
	
	@Override
	public String getSyntax() {
		return "<yes / no question>";
	}
	
	@Override
	public Permission[] getRequiredPermissions() {
		return new Permission[] { Permission.MANAGE_CHANNEL };
	}
}
