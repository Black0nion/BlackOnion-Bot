/**
 *
 */
package com.github.black0nion.blackonionbot.commands.music;

import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.commands.TextCommand;
import com.github.black0nion.blackonionbot.systems.music.MusicSystem;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Objects;

public class NowPlayingCommand extends TextCommand {

  public NowPlayingCommand() {
    this.setCommand("nowplaying");
  }

  @Override
  public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e,
      final Message message, final BlackMember member, final BlackUser author,
      final BlackGuild guild, final TextChannel channel) {
    if (!MusicSystem.channels.containsKey(guild.getIdLong())
        || guild.getTextChannelById(MusicSystem.channels.get(guild.getIdLong())) == null
        || PlayerManager.getInstance()
            .getMusicManager(Objects.requireNonNull(guild
                .getTextChannelById(MusicSystem.channels.get(guild.getIdLong())))).scheduler.player
                    .getPlayingTrack() == null) {
      cmde.error("queueempty", "addsomethingtoqueue");
      return;
    }

    final AudioTrack playingTrack = PlayerManager.getInstance()
        .getMusicManager(Objects.requireNonNull(
            guild.getTextChannelById(MusicSystem.channels.get(guild.getIdLong())))).scheduler.player
                .getPlayingTrack();
    final AudioTrackInfo info = playingTrack.getInfo();

    final String totalDuration = Utils.getDuration((int) (playingTrack.getDuration() / 1000));
    final String currentDuration = Utils.getDuration((int) (playingTrack.getPosition() / 1000));
    final int index = (int) Utils.map(playingTrack.getPosition(), 0, info.length, 0, 17);
    final StringBuilder position = new StringBuilder(Utils.getStringWithNLength("─", 18));
    try {
      position.setCharAt(index, 'D');
    } catch (final Exception ex) {
      ex.printStackTrace();
    }
    final String durationInfo = info.isStream ? ":red_circle: LIVE"
        : "```00:00 " + "(" + position.toString().replace("D", "•") + ") " + totalDuration + "\n"
            + Utils.getStringWithNLength(" ", index + 7 - currentDuration.length() / 2)
            + currentDuration + "\n```";
    cmde.reply(cmde.success().setTitle("Now Playing")
        .setDescription(Utils.escapeMarkdown(info.author + " - " + info.title) + "\n" + durationInfo
            + (!info.isStream
                ? "\nSong will end <t:"
                    + (System.currentTimeMillis() + playingTrack.getDuration()) / 1000 + ":R>"
                : "")));
  }
}
