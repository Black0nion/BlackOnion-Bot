package com.github.black0nion.blackonionbot.commands.slash.impl.music;

import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.systems.music.GuildMusicManager;
import com.github.black0nion.blackonionbot.systems.music.PlayerManager;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class PauseCommand extends SlashCommand {

    public PauseCommand() {
        super("pause", "Pause the current track");
    }

    @Override
    public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, @NotNull BlackMember member, BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
        final GuildVoiceState state = guild.getSelfMember().getVoiceState();
        if (state != null && state.getChannel() != null) {
            //noinspection ConstantConditions - intent is enabled, so it shouldn't be null
            final AudioChannel memberChannel = member.getVoiceState().getChannel();
            if (memberChannel != null && memberChannel.getIdLong() == state.getChannel().getIdLong()) {
                final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getChannel().asTextChannel());
                musicManager.scheduler.player.setPaused(true);

                cmde.success("musicpaused", "useresume", new Placeholder("command", cmde.getCommandHelp()));
            } else {
                cmde.error("notinsamevc", "dontstopotherpplmusic");
            }
        } else {
            cmde.error("notconnected", "startmusictostop");
        }
    }
}