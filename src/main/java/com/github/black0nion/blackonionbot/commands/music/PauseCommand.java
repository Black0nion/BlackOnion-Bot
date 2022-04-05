/**
 *
 */
package com.github.black0nion.blackonionbot.commands.music;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import net.dv8tion.jda.api.entities.*;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.TextCommand;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.music.GuildMusicManager;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * @author _SIM_
 */
public class PauseCommand extends SlashCommand {

  public PauseCommand() {
    super("pause", "Pause the current track");
  }

  @Override
  public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member,
      BlackUser author, BlackGuild guild, TextChannel channel) {
    final GuildVoiceState state = guild.getSelfMember().getVoiceState();
    if (state != null && state.getChannel() != null) {
      // noinspection ConstantConditions - intent is enabled, so it shouldn't be null
      final AudioChannel memberChannel = member.getVoiceState().getChannel();
      if (memberChannel != null && memberChannel.getIdLong() == state.getChannel().getIdLong()) {
        final GuildMusicManager musicManager =
            PlayerManager.getInstance().getMusicManager(e.getTextChannel());
        musicManager.scheduler.player.setPaused(true);

        cmde.success("musicpaused", "useresume", new Placeholder("command",
            CommandEvent.getCommandHelp(guild, CommandBase.commands.get("resume"))));
      } else {
        cmde.error("notinsamevc", "dontstopotherpplmusic");
      }
    } else {
      cmde.error("notconnected", "startmusictostop");
    }
  }
}
