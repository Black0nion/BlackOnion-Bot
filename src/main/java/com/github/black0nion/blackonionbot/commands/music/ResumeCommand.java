/**
 *
 */
package com.github.black0nion.blackonionbot.commands.music;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import net.dv8tion.jda.api.entities.Message;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.music.GuildMusicManager;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * @author _SIM_
 */
public class ResumeCommand extends Command {

    public ResumeCommand() {
	this.setCommand("resume");
    }

    @Override
    public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final GuildVoiceState state = guild.getSelfMember().getVoiceState();
	if (state != null && state.getChannel() != null) {
	    final VoiceChannel memberChannel = member.getVoiceState().getChannel();
	    if (memberChannel != null && memberChannel.getIdLong() == state.getChannel().getIdLong()) {
		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getChannel());
		musicManager.scheduler.player.setPaused(false);

		cmde.success("musicresumed", "itnowgoeson");
	    } else {
		cmde.error("notinsamevc", "dontstopotherpplmusic");
	    }
	} else {
	    cmde.error("notconnected", "startmusictostop");
	}
    }
}