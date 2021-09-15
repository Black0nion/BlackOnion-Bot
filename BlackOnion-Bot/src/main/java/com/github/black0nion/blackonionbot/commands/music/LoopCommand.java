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
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * @author _SIM_
 */
public class LoopCommand extends Command {

    public LoopCommand() {
	this.setCommand("loop");
    }

    @Override
    public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final GuildVoiceState state = guild.getSelfMember().getVoiceState();
	if (state != null && state.getChannel() != null) {
	    final VoiceChannel memberChannel = member.getVoiceState().getChannel();
	    if (memberChannel != null && memberChannel.getIdLong() == state.getChannel().getIdLong()) {
		guild.setLoop(!guild.loopActivated());
		cmde.success("loopswitched", "loopisnow", new Placeholder("status", cmde.getTranslation(guild.loopActivated() ? "on" : "off")));
	    } else {
		cmde.error("notinsamevc", "dontstopotherpplmusic");
	    }
	} else {
	    cmde.error("notconnected", "startmusictostop");
	}
    }
}