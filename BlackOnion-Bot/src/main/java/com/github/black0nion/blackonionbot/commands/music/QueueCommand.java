package com.github.black0nion.blackonionbot.commands.music;

import java.util.List;
import java.util.stream.Collectors;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.systems.music.MusicSystem;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class QueueCommand extends Command {
	
	public QueueCommand() {
		this.setCommand("queue", "playlist");
	}

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		if (!MusicSystem.channels.containsKey(guild.getIdLong()) || guild.getTextChannelById(MusicSystem.channels.get(guild.getIdLong())) == null || PlayerManager.getInstance().getMusicManager(guild.getTextChannelById(MusicSystem.channels.get(guild.getIdLong()))).scheduler.queue.size() == 0) {
			cmde.error("queueempty", "addsomethingtoqueue");
			return;
		}
		
		List<AudioTrack> tracks = PlayerManager.getInstance().getMusicManager(guild.getTextChannelById(MusicSystem.channels.get(guild.getIdLong()))).scheduler.queue.stream().collect(Collectors.toList());
		EmbedBuilder builder = cmde.success();
		if (tracks.size() <= 10) {
        	tracks.forEach(track -> {
        		builder.addField(track.getInfo().title, "By: " + track.getInfo().author, false);
        	});
    	} else {
    		builder.setDescription(LanguageSystem.getTranslation("thistracksplus", author, guild).replace("%tracks%", String.valueOf(tracks.size() - 10)));
    		for (int i = 0; i < tracks.size(); i++) {
    			final AudioTrack track = tracks.get(i);
    			if (i < 10) builder.addField(track.getInfo().title, "By: " + track.getInfo().author, false);
    		}
    	}
		
		message.reply(builder.build()).queue();
	}
}