package com.github.black0nion.blackonionbot.commands.music;

import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import net.dv8tion.jda.api.entities.*;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.music.GuildMusicManager;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * @author _SIM_
 */
public class VolumeCommand extends SlashCommand {

	public VolumeCommand() {
		this.setCommand("volume", "earrapestrength").setSyntax("<volume>").setRequiredArgumentCount(1);
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		final GuildVoiceState state = guild.getSelfMember().getVoiceState();
		if (state != null && state.getChannel() != null) {
			//noinspection ConstantConditions
			if (!member.getVoiceState().inAudioChannel()) {
				cmde.error("You are not in a voice channel!", "dummy lol");
			}
			final AudioChannel memberChannel = member.getVoiceState().getChannel();
			if (memberChannel != null && memberChannel.getIdLong() == state.getChannel().getIdLong()) {
				final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getTextChannel());
				if (Utils.isLong(args[1])) {
					final int volume = Integer.parseInt(args[1]);
					if (volume > 0 && volume <= 150) {
						musicManager.scheduler.player.setVolume(volume);
						cmde.success("volumechanged", "volumesetto", new Placeholder("volume", volume));
					} else {
						cmde.error("invalidvolume", "volumerange");
					}

				} else {
					cmde.error("notanumber", "inputnumber");
				}
			} else {
				cmde.error("notinsamevc", "dontstopotherpplmusic");
			}
		} else {
			cmde.error("notconnected", "startmusictostop");
		}
	}
}