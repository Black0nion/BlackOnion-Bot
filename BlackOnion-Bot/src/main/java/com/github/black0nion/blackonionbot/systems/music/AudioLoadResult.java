package com.github.black0nion.blackonionbot.systems.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class AudioLoadResult implements AudioLoadResultHandler {
	
	private final MusicController controller;
	private final String url;
	
	public AudioLoadResult(MusicController controller, String url) {
		this.controller = controller;
		this.url = url;
	}

	@Override
	public void trackLoaded(AudioTrack track) {
		controller.getPlayer().playTrack(track);
	}

	@Override
	public void playlistLoaded(AudioPlaylist playlist) {
		// IF SEARCH RESULTS DISPLAY MAYBE OTHERWISE START FIRST ONE AND IF NOT SEARCH RESULT PLAY PLAYLISTTTTTTT REEEELOOL
		controller.getPlayer().playTrack(playlist.getTracks().get(0));
	}

	@Override
	public void noMatches() {
	}

	@Override
	public void loadFailed(FriendlyException exception) {
		exception.printStackTrace();
	}

}
