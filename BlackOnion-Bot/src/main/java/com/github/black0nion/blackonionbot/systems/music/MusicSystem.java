package com.github.black0nion.blackonionbot.systems.music;

import java.util.HashMap;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

public class MusicSystem {
	public static AudioPlayerManager audioPlayerManager;
	public static PlayerManager playerManager;
	
	/**
	 * Key: GuildID
	 * Value: ChannelID
	 */
	public static HashMap<Long, Long> musicChannels = new HashMap<>();
	
	public static void init() {
		audioPlayerManager = new DefaultAudioPlayerManager();
		
		playerManager = new PlayerManager();
		
		AudioSourceManagers.registerRemoteSources(audioPlayerManager);
		audioPlayerManager.getConfiguration().setFilterHotSwapEnabled(true);
	}
}
