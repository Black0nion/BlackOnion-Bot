package com.github.black0nion.blackonionbot.systems.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import net.dv8tion.jda.api.entities.Guild;

public class MusicController {
	private Guild guild;
	private AudioPlayer player;
	
	public MusicController(Guild guild) {
		this.guild = guild;
		this.player = MusicSystem.audioPlayerManager.createPlayer();
		
		this.guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
		this.player.setVolume(10);
	}
	
	public Guild getGuild() {
		return guild;
	}
	
	public AudioPlayer getPlayer() {
		return player;
	}
}
