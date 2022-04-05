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

public class ResumeCommand extends SlashCommand {

  public ResumeCommand() {
    super("resume", "Resume playing music");
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
