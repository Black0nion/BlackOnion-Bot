package com.github.black0nion.blackonionbot.commands.music;

import java.util.List;
import java.util.stream.Collectors;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.enums.Category;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.systems.music.MusicSystem;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class QueueCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "queue", "playlist" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		if (!MusicSystem.channels.containsKey(guild.getIdLong()) || guild.getTextChannelById(MusicSystem.channels.get(guild.getIdLong())) == null || PlayerManager.getInstance().getMusicManager(guild.getTextChannelById(MusicSystem.channels.get(guild.getIdLong()))).scheduler.queue.size() == 0) {
			channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("queueempty", "addsomethingtoqueue", false).build()).queue();
			return;
		}
		List<AudioTrack> tracks = PlayerManager.getInstance().getMusicManager(guild.getTextChannelById(MusicSystem.channels.get(guild.getIdLong()))).scheduler.queue.stream().collect(Collectors.toList());
		EmbedBuilder builder = EmbedUtils.getSuccessEmbed(author, guild);
		if (tracks.size() <= 10) {
        	tracks.forEach(track -> {
        		builder.addField(track.getInfo().title, "By: " + track.getInfo().author, false);
        	});
    	} else {
    		builder.setDescription(LanguageSystem.getTranslatedString("thistracksplus", author, guild).replace("%tracks%", String.valueOf(tracks.size() - 10)));
    		for (int i = 0; i < tracks.size(); i++) {
    			final AudioTrack track = tracks.get(i);
    			if (i < 10) builder.addField(track.getInfo().title, "By: " + track.getInfo().author, false);
    		}
    	}
		
		channel.sendMessage(builder.build()).queue();
	}
	
	@Override
	public Category getCategory() {
		return Category.MUSIC;
	}

}
