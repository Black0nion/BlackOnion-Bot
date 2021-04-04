package com.github.black0nion.blackonionbot.systems.music;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class TrackScheduler extends AudioEventAdapter {
    public final AudioPlayer player;
    public final BlockingQueue<AudioTrack> queue;
    public final Guild guild;

    public TrackScheduler(AudioPlayer player, Guild guild) {
    	this.guild = guild;
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack track, AudioManager manager, VoiceChannel vc) {
    	manager.openAudioConnection(vc);
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    public void nextTrack() {
        player.startTrack(queue.poll(), false);
    }
    
    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
    	if (track == null) return;
    	
    	final AudioTrackInfo info = track.getInfo();
    	long seconds = info.length / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		minutes %= 60;
		seconds %= 60;
		guild.getTextChannelById(MusicSystem.channels.get(guild.getIdLong())).sendMessage(EmbedUtils.getSuccessEmbed(null, guild).setTitle(LanguageSystem.getTranslatedString("nowplaying", null, guild) + info.title, info.uri).addField("By: " + info.author, info.isStream ? "STREAM" : LanguageSystem.getTranslatedString("length", null, guild) + (hours > 0 ? hours + "h " : "") + minutes + "min " + seconds + "s", false).build()).queue();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
    	if (endReason.mayStartNext) {
            nextTrack();
        } else {
        	if (queue.isEmpty()) guild.getAudioManager().closeAudioConnection();
        }
    }
}