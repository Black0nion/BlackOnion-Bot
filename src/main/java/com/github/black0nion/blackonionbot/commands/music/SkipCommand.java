package com.github.black0nion.blackonionbot.commands.music;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.systems.music.GuildMusicManager;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public class SkipCommand extends SlashCommand {

    public SkipCommand() {
        super(builder(Commands.slash("skip", "Skip what you're currently playing.")));
    }

    @Override
    public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
        final GuildVoiceState state = guild.getSelfMember().getVoiceState();
        if (state != null && state.getChannel() != null) {
            final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getChannel().asTextChannel());
            final AudioPlayer player = musicManager.audioPlayer;

            if (player.getPlayingTrack() == null) {
                cmde.error("wiat what", "why am i even in here? i should've gotten disconnected, lol");
                return;
            }

            final AudioTrackInfo previousTrack = musicManager.scheduler.player.getPlayingTrack().getInfo();
            if (musicManager.scheduler.queue.peek() != null) {
                musicManager.scheduler.nextTrack();
                final AudioTrackInfo newTrack = musicManager.scheduler.player.getPlayingTrack().getInfo();
                cmde.success("songskipped", "songgotskipped",
                        new Placeholder("oldsong", previousTrack.author + " - " + previousTrack.title),
                        new Placeholder("newsong", newTrack.author + " - " + newTrack.title));
            } else {
                musicManager.audioPlayer.stopTrack();
                guild.getAudioManager().closeAudioConnection();
                cmde.success("songskipped", "queueemptyskip");
            }
        } else {
            cmde.error("notconnected", "startmusictostop");
        }
    }
}