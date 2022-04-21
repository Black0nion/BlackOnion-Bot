package com.github.black0nion.blackonionbot.commands.music;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.systems.music.MusicSystem;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class QueueCommand extends SlashCommand {

	public QueueCommand() {
		super(builder(Commands.slash("queue", "Tells you the current queue.")));
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
		if (!MusicSystem.channels.containsKey(guild.getIdLong()) || guild.getTextChannelById(MusicSystem.channels.get(guild.getIdLong())) == null || PlayerManager.getInstance().getMusicManager(Objects.requireNonNull(guild.getTextChannelById(MusicSystem.channels.get(guild.getIdLong())))).scheduler.queue.size() == 0) {
			cmde.error("queueempty", "addsomethingtoqueue");
			return;
		}

		final List<AudioTrack> tracks = PlayerManager.getInstance().getMusicManager(Objects.requireNonNull(guild.getTextChannelById(MusicSystem.channels.get(guild.getIdLong())))).scheduler.queue.stream().toList();
		final EmbedBuilder builder = cmde.success();
		if (tracks.size() <= 10) {
			tracks.forEach(track -> builder.addField(track.getInfo().title, "By: " + track.getInfo().author, false));
		} else {
			builder.setDescription(cmde.getTranslation("thistracksplus").replace("%tracks%", String.valueOf(tracks.size() - 10)));
			for (int i = 0; i < tracks.size(); i++) {
				final AudioTrack track = tracks.get(i);
				if (i < 10) {
					builder.addField(track.getInfo().title, "By: " + track.getInfo().author, false);
				}
			}
		}

		cmde.reply(builder);
	}
}