package com.github.black0nion.blackonionbot.systems.music;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.methods.TrackRequest;
import com.wrapper.spotify.methods.authentication.ClientCredentialsGrantRequest;
import com.wrapper.spotify.models.ClientCredentials;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class PlayerManager {
    private static PlayerManager INSTANCE;

    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;
    
    private static Api spotifyApi;

	public static void init() {
		if (!Bot.getCredentialsManager().has("spotify_client_id") || !Bot.getCredentialsManager().has("spotify_client_secret")) {
			System.out.println("No Spotify API Key specified! You won't be able to play spotify tracks!");
			return;
		}
		
		spotifyApi = new Api.Builder()
				.clientId(Bot.getCredentialsManager().getString("spotify_client_id"))
				.clientSecret(Bot.getCredentialsManager().getString("spotify_client_secret"))
				.build();
		
		new Timer().scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					try {
						ClientCredentialsGrantRequest clientCredentialsRequest = spotifyApi.clientCredentialsGrant().build();
						ClientCredentials credentials = clientCredentialsRequest.get();
						spotifyApi.setAccessToken(credentials.getAccessToken());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, 0, 3500000);
	}
	
    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(TextChannel channel) {
        return this.musicManagers.computeIfAbsent(channel.getGuild().getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager, BlackGuild.from(channel.getGuild()));

            channel.getGuild().getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

            return guildMusicManager;
        });
    }

    public void loadAndPlay(BlackUser author, TextChannel channel, String trackUrl, AudioManager manager, VoiceChannel vc) {
        final GuildMusicManager musicManager = this.getMusicManager(channel);
        final BlackGuild guild = BlackGuild.from(channel.getGuild());
        MusicSystem.channels.put(guild.getIdLong(), channel.getIdLong());

        if (trackUrl.contains("spotify.com")) {
        	String[] parsed = trackUrl.split("/track/");
        	if (parsed.length == 2) {
        		final TrackRequest request = spotifyApi.getTrack(parsed[1]).build();
        		try {
        			trackUrl = "ytsearch:" + request.get().getName();
        		} catch (Exception e) {
        			e.printStackTrace();
        		}
        	}
        }
        
        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.scheduler.queue(track, manager, vc);
                channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild).addField("addedtoqueue", track.getInfo().title + " by " + track.getInfo().author, false).build()).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                final List<AudioTrack> tracks = playlist.getTracks();
                
                if (playlist.isSearchResult()) {
                	EmbedBuilder builder = EmbedUtils.getSuccessEmbed(author, guild);
        			List<AudioTrack> trackz = playlist.getTracks().subList(0, (playlist.getTracks().size() > 10 ? 9 : playlist.getTracks().size()));
        			for (int i = 0; i < trackz.size(); i++) {
        				AudioTrack track = trackz.get(i);
        				builder.addField(Utils.emojis[i] + " " + track.getInfo().title, "By: " + track.getInfo().author, false);
        			}
        			channel.sendMessage(builder.build()).queue((msgg) -> { BlackMessage msg = BlackMessage.from(msgg); for (int i=0;i<trackz.size();i++) msg.addReaction(Utils.numbersUnicode.get(i)).queue(); retry(author, msg, trackz, musicManager, manager, vc);});
                } else {
                	EmbedBuilder builder = EmbedUtils.getSuccessEmbed(author, guild);
                	
                	builder.setTitle("addedtoqueue");
                	
                	if (tracks.size() <= 10) {
	                	tracks.forEach(track -> {
	                		builder.addField(track.getInfo().title, "By: " + track.getInfo().author, false);
		                	musicManager.scheduler.queue(track, manager, vc);
	                	});
                	} else {
                		builder.setDescription(LanguageSystem.getTranslation("thistracksplusadded", author, guild).replace("%tracks%", String.valueOf(tracks.size() - 10)));
                		for (int i = 0; i < tracks.size(); i++) {
                			final AudioTrack track = tracks.get(i);
							musicManager.scheduler.queue(track, manager, vc);
                			if (i < 10) builder.addField(track.getInfo().title, "By: " + track.getInfo().author, false);
                		}
                	}
                	
	                channel.sendMessage(builder.build()).queue();
                }
            }

            @Override
            public void noMatches() {
            	channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("notfound", "musicnotfound", false).build()).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
				channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("errorhappened", "somethingwentwrong", false).build()).queue();
                exception.printStackTrace();
            }
        });
    }
    
    private void retry(BlackUser author, BlackMessage msg, List<AudioTrack> tracks, GuildMusicManager musicManager, AudioManager manager, VoiceChannel vc) {
		CommandBase.waiter.waitForEvent(GuildMessageReactionAddEvent.class, 
			(event) -> msg.getIdLong() == event.getMessageIdLong() && !event.getUser().isBot(), 
			event -> {
				event.getReaction().removeReaction(event.getUser()).queue();
				if (!event.getReactionEmote().isEmoji() || !Utils.numbersUnicode.containsValue(event.getReactionEmote().getAsCodepoints()) || tracks.size() < Utils.numbersUnicode.entrySet().stream().filter((entry) -> {return entry.getValue().equals(event.getReactionEmote().getAsCodepoints());}).findFirst().get().getKey()) {
					retry(author, msg, tracks, musicManager, manager, vc);
					return;
				}
				final AudioTrack track = tracks.get(Utils.numbersUnicode.entrySet().stream().filter((entry) -> {return entry.getValue().equals(event.getReactionEmote().getAsCodepoints());}).findFirst().get().getKey());
				musicManager.scheduler.queue(track, manager, vc);
		}, 1, TimeUnit.MINUTES, () -> msg.editMessage(EmbedUtils.getErrorEmbed(author, msg.getBlackGuild()).addField("timeout", "tooktoolong", false).build()).queue());
	}

    public static PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;
    }
}