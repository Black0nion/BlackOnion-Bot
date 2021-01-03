package com.github.ahitm_2020_2025.blackonionbot.commands.impl.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import com.github.ahitm_2020_2025.blackonionbot.Command;
import com.github.ahitm_2020_2025.blackonionbot.enums.Category;
import com.github.ahitm_2020_2025.blackonionbot.enums.Progress;
import com.github.ahitm_2020_2025.blackonionbot.lavaplayer.GuildMusicManager;
import com.github.ahitm_2020_2025.blackonionbot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SkipCommand implements Command {

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, MessageChannel channel) {
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
		
		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());
		final AudioPlayer audioPlayer = musicManager.audioPlayer;
		
		if (audioPlayer.getPlayingTrack() == null) {
			channel.sendMessage("Es wird derzeit kein Song gespielt!").queue();
			return;
		}
		
		musicManager.scheduler.nextTrack();
		if (musicManager.scheduler.queue.peek() != null) {
			channel.sendMessage("Jetzt spielt: `" + audioPlayer.getPlayingTrack().getInfo().title + "` von `" + audioPlayer.getPlayingTrack().getInfo().author + "`").queue();
		} else {
			musicManager.audioPlayer.stopTrack();
			channel.sendMessage("Die Queue ist leer!").queue();
		}
		
	}

	@Override
	public String getDescription() {
		return "Skipt ein Lied";
	}
	
	@Override
	public Category getCategory() {
		return Category.MUSIC;
	}
	
	@Override
	public String[] getCommand() {
		return new String[] {"skip"};
	}

	@Override
	public Progress getProgress() {
		return Progress.PAUSED;
	}
}
