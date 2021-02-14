package com.github.black0nion.blackonionbot.systems.music;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class TrackScheduler extends AudioEventAdapter {
	
	private final AudioPlayer player;
	private final BlockingQueue<AudioTrack> queue;
	
	public TrackScheduler(AudioPlayer player) {
		this.player = player;
		this.queue = new LinkedBlockingQueue<>();
	}
	
	public void queue(AudioTrack track) {
		if (!this.player.startTrack(track, true))
			this.queue.offer(track);
	}
	
	public void nextTrack() {
		player.startTrack(queue.poll(), false);
	}
	
	@Override
	public void onPlayerPause(AudioPlayer player) {
	}
	
	@Override
	public void onPlayerResume(AudioPlayer player) {
	}
	
	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {
		long guildid = MusicSystem.playerManager.getGuildByPlayerHash(player.hashCode());
		Guild guild = Bot.jda.getGuildById(guildid);
		
		AudioTrackInfo info = track.getInfo();
		EmbedBuilder builder = EmbedUtils.getSuccessEmbed(null, guild);
		
		long seconds = info.length / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		minutes %= 60;
		seconds %= 60;
		
		String url = info.uri;
		builder.setTitle("nowplaying");
		builder.addField(info.author, "[" + info.title + "](" + url + ")", false);
		builder.addField("length", info.isStream ? "STREAM" : (hours > 0 ? hours + "h " : "") + minutes + "min " + seconds + "s", true);
		
		if (url.startsWith("https://www.youtube.com/watch?v=")) {
			String videoId = url.replace("https://www.youtube.com/watch?v=", "");
			
			InputStream stream;
			try {
				stream = new URL("https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg").openStream();
				
				builder.setImage("attachment://thumbnail.png");
				TextChannel channel = guild.getTextChannelById(MusicSystem.musicChannels.get(guild.getIdLong()));
				if (channel != null) {
					channel.sendFile(stream, "thumbnail.png").embed(builder.build()).queue();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			TextChannel channel = guild.getTextChannelById(MusicSystem.musicChannels.get(guild.getIdLong()));
			if (channel != null) {
				channel.sendMessage(builder.build()).queue();
			}
		}
	}
	
	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if (endReason.mayStartNext)
			nextTrack();
	}
}
