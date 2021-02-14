package com.github.black0nion.blackonionbot.commands.music.old;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.enums.Category;
import com.github.black0nion.blackonionbot.enums.Progress;
import com.github.black0nion.blackonionbot.systems.music.GuildMusicManager;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class StopCommand implements Command {

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		final GuildVoiceState selfVoiceState = e.getGuild().getSelfMember().getVoiceState();
		if (!selfVoiceState.inVoiceChannel()) {
			channel.sendMessage("Ich spiele gerade keine Musik!").queue();
			return;
		} else if (!member.getVoiceState().inVoiceChannel()) {
			channel.sendMessage("Du bist nicht in einem Voicechannel!").queue();
			return;
		} else if (member.getVoiceState().getChannel() != selfVoiceState.getChannel()){
			channel.sendMessage("Du bist nicht in meinem Voicechannel!").queue();
			return;
		}
		
		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getTextChannel());
		
		musicManager.scheduler.player.stopTrack();
		
		musicManager.scheduler.queue.clear();
		
		channel.sendMessage("Die Musik wurde gestoppt.").queue();
	}
	
	@Override
	public Category getCategory() {
		return Category.MUSIC;
	}

	@Override
	public String[] getCommand() {
		return new String[] {"stop"};
	}
	
	@Override
	public Progress getProgress() {
		return Progress.PAUSED;
	}
}
