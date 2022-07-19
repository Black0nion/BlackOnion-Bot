package com.github.black0nion.blackonionbot.commands.music;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.systems.music.GuildMusicManager;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

/**
 * @author _SIM_
 */
public class VolumeCommand extends SlashCommand {
    private static final String VOLUME = "volume";

    public VolumeCommand() {
        super(builder(Commands.slash("volume", "Set the volume of the player")
                .addOptions(new OptionData(OptionType.INTEGER, VOLUME, "The volume to set the player to", true)
                        .setRequiredRange(1, 100))));
    }

    @Override
    public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, @NotNull BlackMember member, BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
        var volume = e.getOption(VOLUME, OptionMapping::getAsInt);
        final GuildVoiceState state = guild.getSelfMember().getVoiceState();
        if (state != null && state.getChannel() != null) {
            if (!member.getVoiceState().inAudioChannel()) {
                cmde.error("You are not in a voice channel!", "dummy lol");
            }

            final AudioChannel memberChannel = member.getVoiceState().getChannel();

            if (memberChannel != null && memberChannel.getIdLong() == state.getChannel().getIdLong()) {
                final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getTextChannel());
                musicManager.scheduler.player.setVolume(volume);
                cmde.success("volumechanged", "volumesetto", new Placeholder("volume", volume));
            } else {
                cmde.error("notinsamevc", "dontstopotherpplmusic");
            }

        } else {
            cmde.error("notconnected", "startmusictostop");
        }
    }
}