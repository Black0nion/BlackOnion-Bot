/**
 *
 */
package com.github.black0nion.blackonionbot.slashcommands.music;

import com.github.black0nion.blackonionbot.slashcommands.SlashCommand;
import com.github.black0nion.blackonionbot.slashcommands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.systems.music.MusicSystem;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class NowPlayingCommand extends SlashCommand {

    public NowPlayingCommand() {
        super(builder(Commands.slash("nowplaying", "Info about the current track.")));
    }

    @Override
    public void execute(@NotNull SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
        if (!MusicSystem.channels.containsKey(guild.getIdLong()) || guild.getTextChannelById(MusicSystem.channels.get(guild.getIdLong())) == null || PlayerManager.getInstance().getMusicManager(Objects.requireNonNull(guild.getTextChannelById(MusicSystem.channels.get(guild.getIdLong())))).scheduler.player.getPlayingTrack() == null) {
            cmde.error("queueempty", "addsomethingtoqueue");
            return;
        }

        final AudioTrack playingTrack = PlayerManager.getInstance().getMusicManager(Objects.requireNonNull(guild.getTextChannelById(MusicSystem.channels.get(guild.getIdLong())))).scheduler.player.getPlayingTrack();
        final AudioTrackInfo info = playingTrack.getInfo();

        final String totalDuration = Utils.getDuration((int) (playingTrack.getDuration() / 1000));
        final String currentDuration = Utils.getDuration((int) (playingTrack.getPosition() / 1000));
        final int index = (int) Utils.map(playingTrack.getPosition(), 0, info.length, 0, 17);
        final StringBuilder position = new StringBuilder("-".repeat(18));
        try {
            position.setCharAt(index, 'D');
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
        final String durationInfo = info.isStream ? ":red_circle: LIVE" : "```00:00 " +
                "(" + position.toString().replace("D", "•") + ") " + totalDuration + "\n" +
                " ".repeat(index + 7 - currentDuration.length() / 2) + currentDuration + "\n```";
        cmde.reply(cmde.success().setTitle("Now Playing").setDescription(Utils.escapeMarkdown(info.author + " - " + info.title) + "\n" + durationInfo + (!info.isStream ? "\nSong will end <t:" + (System.currentTimeMillis() + playingTrack.getDuration()) / 1000 + ":R>" : "")));
    }
}