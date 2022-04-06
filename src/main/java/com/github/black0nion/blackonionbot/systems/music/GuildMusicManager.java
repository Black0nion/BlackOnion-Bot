package com.github.black0nion.blackonionbot.systems.music;

import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class GuildMusicManager {
	public final AudioPlayer audioPlayer;

	public final TrackScheduler scheduler;

	public final AudioPlayerSendHandler sendHandler;

	public final BlackGuild guild;

	public GuildMusicManager(final AudioPlayerManager manager, final BlackGuild guild) {
		this.audioPlayer = manager.createPlayer();
		this.scheduler = new TrackScheduler(this.audioPlayer, guild);
		this.audioPlayer.addListener(this.scheduler);
		this.sendHandler = new AudioPlayerSendHandler(this.audioPlayer);
		this.guild = guild;
	}

	/**
	 * @return Wrapper around AudioPlayer to use it as an AudioSendHandler.
	 */
	public AudioPlayerSendHandler getSendHandler() {
		return sendHandler;
	}
}
