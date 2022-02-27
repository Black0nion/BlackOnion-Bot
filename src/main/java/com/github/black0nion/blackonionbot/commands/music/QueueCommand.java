package com.github.black0nion.blackonionbot.commands.music;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.music.MusicSystem;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Objects;

public class QueueCommand extends Command {

	public QueueCommand() {
		this.setCommand("queue", "playlist");
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		if (!MusicSystem.channels.containsKey(guild.getIdLong()) || guild.getTextChannelById(MusicSystem.channels.get(guild.getIdLong())) == null || PlayerManager.getInstance().getMusicManager(Objects.requireNonNull(guild.getTextChannelById(MusicSystem.channels.get(guild.getIdLong())))).scheduler.queue.size() == 0) {
			cmde.error("queueempty", "addsomethingtoqueue");
			return;
		}

		final List<AudioTrack> tracks = PlayerManager.getInstance().getMusicManager(Objects.requireNonNull(guild.getTextChannelById(MusicSystem.channels.get(guild.getIdLong())))).scheduler.queue.stream().toList();
		final EmbedBuilder builder = cmde.success();
		if (tracks.size() <= 10) {
			tracks.forEach(track -> {
				builder.addField(track.getInfo().title, "By: " + track.getInfo().author, false);
			});
		} else {
			builder.setDescription(cmde.getTranslation("thistracksplus").replace("%tracks%", String.valueOf(tracks.size() - 10)));
			for (int i = 0; i < tracks.size(); i++) {
				final AudioTrack track = tracks.get(i);
				if (i < 10) {
					builder.addField(track.getInfo().title, "By: " + track.getInfo().author, false);
				}
			}
		}

		message.replyEmbeds(builder.build()).queue();
	}
}