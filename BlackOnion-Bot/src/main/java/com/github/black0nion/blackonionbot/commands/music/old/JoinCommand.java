package com.github.black0nion.blackonionbot.commands.music.old;

import com.github.black0nion.blackonionbot.commands.Command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class JoinCommand implements Command {

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		final Member self = e.getGuild().getSelfMember();
		final GuildVoiceState selfVoiceState = self.getVoiceState();
		
		if (selfVoiceState.inVoiceChannel()) {
			channel.sendMessage("Ich bin bereits in einem Voicechannel!").queue();
			return;
		}
		
		if (!member.getVoiceState().inVoiceChannel()) {
			channel.sendMessage("Du bist in keinem Voicechannel!").queue();
			return;
		}
		
		final AudioManager audioManager = e.getGuild().getAudioManager();
		final VoiceChannel memberChannel = member.getVoiceState().getChannel();
		
		audioManager.openAudioConnection(memberChannel);
	}
	
	@Override
	public String[] getCommand() {
		return new String[] {"join"};
	}

}
