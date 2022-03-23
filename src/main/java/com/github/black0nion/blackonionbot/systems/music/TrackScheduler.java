package com.github.black0nion.blackonionbot.systems.music;

import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class TrackScheduler extends AudioEventAdapter {
	public final AudioPlayer player;
	public final BlockingQueue<AudioTrack> queue;
	public final BlackGuild guild;

	public TrackScheduler(final AudioPlayer player, final BlackGuild guild) {
		this.guild = guild;
		this.player = player;
		this.queue = new LinkedBlockingQueue<>();
	}

	public void queue(final AudioTrack track, final AudioManager manager, final AudioChannel vc) {
		manager.openAudioConnection(vc);
		if (!this.player.startTrack(track, true)) {
			this.queue.offer(track);
		} else if (this.guild.loopActivated()) {
			this.queue.offer(track.makeClone());
		}
	}

	public void nextTrack() {
		final AudioTrack poll = this.queue.poll();
		if (poll == null) {
			this.guild.getAudioManager().closeAudioConnection();
		} else {
			this.player.startTrack(poll, false);
			if (this.guild.loopActivated()) {
				this.queue.offer(poll.makeClone());
			}
		}
	}

	@Override
	public void onTrackStart(final AudioPlayer player, final AudioTrack track) {
		if (track == null) return;

		final AudioTrackInfo info = track.getInfo();
		long seconds = info.length / 1000;
		long minutes = seconds / 60;
		final long hours = minutes / 60;
		minutes %= 60;
		seconds %= 60;
		if (!(this.queue.size() == 1 && this.guild.loopActivated())) {
			this.guild.getTextChannelById(MusicSystem.channels.get(this.guild.getIdLong())).sendMessageEmbeds(EmbedUtils.getSuccessEmbed(null, this.guild).setTitle(LanguageSystem.getTranslation("nowplaying", null, this.guild) + info.title, info.uri).addField("By: " + info.author, info.isStream ? "STREAM" : LanguageSystem.getTranslation("length", null, this.guild) + (hours > 0 ? hours + "h " : "") + minutes + "min " + seconds + "s", false).build()).queue();
		}
	}

	@Override
	public void onTrackEnd(final AudioPlayer player, final AudioTrack track, final AudioTrackEndReason endReason) {
		if (endReason.mayStartNext) {
			this.nextTrack();
		} else if (this.queue.isEmpty() && endReason != AudioTrackEndReason.REPLACED) {
			this.guild.getAudioManager().closeAudioConnection();
		}
	}
}