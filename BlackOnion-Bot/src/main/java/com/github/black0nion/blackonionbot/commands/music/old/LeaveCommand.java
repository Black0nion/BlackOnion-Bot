package com.github.black0nion.blackonionbot.commands.music.old;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.enums.Category;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class LeaveCommand implements Command {

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		if (!member.hasPermission(Permission.MANAGE_CHANNEL)) {
			channel.sendMessage("Du darfst das nicht!").queue();
		}
		if (e.getGuild().getSelfMember().getVoiceState().inVoiceChannel()) {
			e.getGuild().getAudioManager().closeAudioConnection();
			channel.sendMessage("Ich habe den Kanal verlassen!").queue();
		}
	}
	
	@Override
	public Category getCategory() {
		return Category.MUSIC;
	}
	
	@Override
	public String[] getCommand() {
		return new String[] {"leave", "disconnect"};
	}

}
