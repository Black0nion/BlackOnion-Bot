package com.github.black0nion.blackonionbot.commands.music;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import net.dv8tion.jda.api.entities.Message;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.music.GuildMusicManager;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * @author _SIM_
 *
 */
public class VolumeCommand extends Command {

    public VolumeCommand() {
	this.setCommand("volume", "earrapestrength").setSyntax("<volume>").setRequiredArgumentCount(1);
    }

    @Override
    public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final GuildVoiceState state = guild.getSelfMember().getVoiceState();
	if (state != null && state.getChannel() != null) {
	    final VoiceChannel memberChannel = member.getVoiceState().getChannel();
	    if (memberChannel != null && memberChannel.getIdLong() == state.getChannel().getIdLong()) {
		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getChannel());
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