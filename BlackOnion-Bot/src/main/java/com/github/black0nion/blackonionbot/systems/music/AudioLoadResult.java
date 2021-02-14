package com.github.black0nion.blackonionbot.systems.music;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class AudioLoadResult implements AudioLoadResultHandler {
	
	public static EventWaiter waiter;
	
	private static final HashMap<Integer, String> numbersUnicode = new HashMap<>();
	
	static {
		numbersUnicode.put(0, "U+30U+fe0fU+20e3");
		numbersUnicode.put(1, "U+31U+fe0fU+20e3");
		numbersUnicode.put(2, "U+32U+fe0fU+20e3");
		numbersUnicode.put(3, "U+33U+fe0fU+20e3"); 
		numbersUnicode.put(4, "U+34U+fe0fU+20e3");
		numbersUnicode.put(5, "U+35U+fe0fU+20e3");
		numbersUnicode.put(6, "U+36U+fe0fU+20e3");
		numbersUnicode.put(7, "U+37U+fe0fU+20e3");
		numbersUnicode.put(8, "U+38U+fe0fU+20e3");
		numbersUnicode.put(9, "U+39U+fe0fU+20e3");
		numbersUnicode.put(10,"U+1F51F");
	};
	
	private static final String[] emojis = new String[] { ":one:", ":two:", ":three:", ":four:", ":five:", ":six:", ":seven:", ":eight:", ":nine:", ":ten:" };
	
	private final MusicController controller;
	@SuppressWarnings("unused")
	private final String url;
	private final Guild guild;
	
	public AudioLoadResult(MusicController controller, String url) {
		this.controller = controller;
		this.url = url;
		this.guild = controller.getGuild();
	}

	@Override
	public void trackLoaded(AudioTrack track) {
		controller.getPlayer().playTrack(track);
	}

	@Override
	public void playlistLoaded(AudioPlaylist playlist) {
		if (playlist.isSearchResult()) {
			EmbedBuilder builder = EmbedUtils.getSuccessEmbed(null, guild);
			List<AudioTrack> tracks = playlist.getTracks().subList(0, (playlist.getTracks().size() > 10 ? 9 : playlist.getTracks().size()));
			for (int i = 0; i < tracks.size(); i++) {
				AudioTrack track = tracks.get(i);
				builder.addField(emojis[i] + " " + track.getInfo().title, "By: " + track.getInfo().author, false);
			}
			guild.getTextChannelById(MusicSystem.musicChannels.get(guild.getIdLong())).sendMessage(builder.build()).queue((msg) -> {for (int i=0;i<tracks.size();i++) msg.addReaction(numbersUnicode.get(i)).queue(); retry(msg, tracks);});
			
		} else {
			controller.getPlayer().playTrack(playlist.getTracks().get(0));
		}
	}
	
	private void retry(Message msg, List<AudioTrack> tracks) {
		waiter.waitForEvent(GuildMessageReactionAddEvent.class, 
			(event) -> msg.getIdLong() == event.getMessageIdLong() && !event.getUser().isBot(), 
			event -> {
				if (!event.getReactionEmote().isEmoji() || !numbersUnicode.containsValue(event.getReactionEmote().getAsCodepoints()) || tracks.size() < numbersUnicode.entrySet().stream().filter((entry) -> {return entry.getValue().equals(event.getReactionEmote().getAsCodepoints());}).findFirst().get().getKey()) {
					retry(msg, tracks);
					return;
				}
				controller.getPlayer().playTrack(tracks.get(numbersUnicode.entrySet().stream().filter((entry) -> {return entry.getValue().equals(event.getReactionEmote().getAsCodepoints());}).findFirst().get().getKey()));
		}, 1, TimeUnit.MINUTES, () -> msg.editMessage(EmbedUtils.getErrorEmbed(null, guild).addField("timeout", "tooktoolong", false).build()).queue());
	}

	@Override
	public void noMatches() {
		guild.getTextChannelById(MusicSystem.musicChannels.get(guild.getIdLong())).sendMessage(EmbedUtils.getErrorEmbed(null, guild).addField("notfound", "musicnotfound", false).build()).queue();
	}

	@Override
	public void loadFailed(FriendlyException exception) {
		guild.getTextChannelById(MusicSystem.musicChannels.get(guild.getIdLong())).sendMessage(EmbedUtils.getErrorEmbed(null, guild).addField("errorhappened", exception.getMessage() != null ? exception.getMessage() : "somethingwentwrong", false).build()).queue();
		exception.printStackTrace();
	}

}
