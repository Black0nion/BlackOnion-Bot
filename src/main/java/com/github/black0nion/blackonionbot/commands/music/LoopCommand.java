/**
 *
 */
package com.github.black0nion.blackonionbot.commands.music;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * @author _SIM_
 */
public class LoopCommand extends SlashCommand {

	public LoopCommand() {
		super("loop", "Loops the current queue");
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author,
			BlackGuild guild, TextChannel channel) {
		final GuildVoiceState state = guild.getSelfMember().getVoiceState();
		if (state != null && state.getChannel() != null) {
			// noinspection ConstantConditions - intent is enabled, so it shouldn't be null
			final AudioChannel memberChannel = member.getVoiceState().getChannel();
			if (memberChannel != null && memberChannel.getIdLong() == state.getChannel().getIdLong()) {
				guild.setLoop(!guild.loopActivated());
				cmde.success("loopswitched", "loopisnow",
						new Placeholder("status", cmde.getTranslation(guild.loopActivated() ? "on" : "off")));
			} else {
				cmde.error("notinsamevc", "dontstopotherpplmusic");
			}
		} else {
			cmde.error("notconnected", "startmusictostop");
		}
	}
}
