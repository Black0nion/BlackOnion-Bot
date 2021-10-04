package com.github.black0nion.blackonionbot.commands.music;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.systems.music.GuildMusicManager;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

/**
 * @author _SIM_
 *
 */
public class VolumeCommand extends SlashCommand {

    public VolumeCommand() {
	this.setData(new CommandData("volume", "Set the volume of the music player").addOption(OptionType.INTEGER, "volume", "The volume to set the player to"));
    }

    @Override
        public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final GuildVoiceState state = guild.getSelfMember().getVoiceState();
	if (state != null && state.getChannel() != null) {
	    final VoiceChannel memberChannel = member.getVoiceState().getChannel();
	    if (memberChannel != null && memberChannel.getIdLong() == state.getChannel().getIdLong()) {
		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(channel);
		if (!e.getOptionsByType(OptionType.INTEGER).isEmpty()) {
		    final long volume = e.getOptionsByType(OptionType.INTEGER).get(0).getAsLong();
		    if (volume > 0 && volume <= 150) {
			musicManager.scheduler.player.setVolume((int) volume);
			cmde.success("volumechanged", "volumesetto", new Placeholder("volume", volume));
		    } else {
			cmde.error("invalidvolume", "volumerange");
		    }
		} else {
		    cmde.success("currentvolume", "thecurrentvolume", new Placeholder("volume", musicManager.scheduler.player.getVolume()));
		}
	    } else {
		cmde.error("notinsamevc", "dontstopotherpplmusic");
	    }
	} else {
	    cmde.error("notconnected", "startmusictostop");
	}
    }
}