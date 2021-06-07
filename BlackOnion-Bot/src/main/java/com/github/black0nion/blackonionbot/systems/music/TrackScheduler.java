package com.github.black0nion.blackonionbot.systems.music;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class TrackScheduler extends AudioEventAdapter {
    public final AudioPlayer player;
    public final BlockingQueue<AudioTrack> queue;
    public final BlackGuild guild;

    public TrackScheduler(final AudioPlayer player, final BlackGuild guild) {
	this.guild = guild;
	this.player = player;
	this.queue = new LinkedBlockingQueue<>();
    }

    public void queue(final AudioTrack track, final AudioManager manager, final VoiceChannel vc) {
	manager.openAudioConnection(vc);
	if (!player.startTrack(track, true)) {
	    queue.offer(track);
	} else if (guild.loopActivated()) {
	    queue.offer(track.makeClone());
	}
    }

    public void nextTrack() {
	final AudioTrack poll = queue.poll();
	if (guild.loopActivated()) {
	    queue.offer(poll.makeClone());
	}
	if (poll == null) {
	    this.guild.getAudioManager().closeAudioConnection();
	} else {
	    player.startTrack(poll, false);
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
	if (!(queue.size() == 1 && guild.loopActivated())) {
	    guild.getTextChannelById(MusicSystem.channels.get(guild.getIdLong())).sendMessage(EmbedUtils.getSuccessEmbed(null, guild).setTitle(LanguageSystem.getTranslation("nowplaying", null, guild) + info.title, info.uri).addField("By: " + info.author, info.isStream ? "STREAM" : LanguageSystem.getTranslation("length", null, guild) + (hours > 0 ? hours + "h " : "") + minutes + "min " + seconds + "s", false).build()).queue();
	}
    }

    @Override
    public void onTrackEnd(final AudioPlayer player, final AudioTrack track, final AudioTrackEndReason endReason) {
	if (endReason.mayStartNext) {
	    nextTrack();
	} else if (queue.isEmpty()) {
	    guild.getAudioManager().closeAudioConnection();
	}
    }
}