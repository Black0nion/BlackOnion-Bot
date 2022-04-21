package com.github.black0nion.blackonionbot.commands.music;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.systems.music.GuildMusicManager;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class StopCommand extends SlashCommand {

	public StopCommand() {
		super("stop", "Stop the currently playing music");
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, @NotNull BlackMember member, BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
		final GuildVoiceState state = guild.getSelfMember().getVoiceState();
		if (state != null && state.getChannel() != null) {
			//noinspection ConstantConditions - intent is enabled, so it shouldn't be null
			final AudioChannel memberChannel = member.getVoiceState().getChannel();
			if (memberChannel != null && memberChannel.getIdLong() == state.getChannel().getIdLong()) {
				final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getTextChannel());
				musicManager.scheduler.player.stopTrack();
				musicManager.scheduler.queue.clear();
				guild.getAudioManager().closeAudioConnection();

				cmde.success("musicstopped", "leftvc");
			} else {
				cmde.error("notinsamevc", "dontstopotherpplmusic");
			}
		} else {
			cmde.error("notconnected", "startmusictostop");
		}
	}
}