package com.github.black0nion.blackonionbot.commands.moderation.joinleave;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SetWelcomeChannelCommand extends SlashCommand {
    private static final String SET_WELCOME_COMMAND = "set_welcome_channel";
    private static final String SET_WELCOME_CHANNEL = "channel";
    private static final String CHANGE_WELCOME_COMMAND = "change_welcome_channel";
    private static final String CHANGE_WELCOME_CHANNEL = "channel";
    private static final String REMOVE_WELCOME_COMMAND = "remove_welcome_channel";

    public SetWelcomeChannelCommand() {
        super(builder(Commands
                .slash("welcome_channel", "Used to to set/remove/change welcome leave channel for the guild.")
                .addSubcommands(
                        new SubcommandData(SET_WELCOME_COMMAND, "Used to set the welcome channel for the guild.")
                                .addOption(OptionType.CHANNEL, SET_WELCOME_CHANNEL,
                                        "The channel to set as the welcome channel.", true),
                        new SubcommandData(CHANGE_WELCOME_COMMAND, "Used to change the welcome channel for the guild.")
                                .addOption(OptionType.CHANNEL, CHANGE_WELCOME_CHANNEL,
                                        "The channel to change the welcome channel to.", true),
                        new SubcommandData(REMOVE_WELCOME_COMMAND,
                                "Used to remove the welcome channel for the guild.")))
                .setRequiredBotPermissions(Permission.MESSAGE_MANAGE)
                .setRequiredPermissions(Permission.MANAGE_SERVER));
    }

    @Override
    public void execute(@NotNull SlashCommandEvent event, @NotNull SlashCommandInteractionEvent e, BlackMember member,
                        BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
        switch (Objects.requireNonNull(e.getSubcommandName())) {
            case SET_WELCOME_COMMAND -> setWelcomeChannel(event, e, guild);
            case CHANGE_WELCOME_COMMAND -> changeWelcomeChannel(event, e, guild);
            case REMOVE_WELCOME_COMMAND -> removeWelcomeChannel(event, e, guild);
            default -> event.send("invalidsubcommand");
        }
    }

    public void setWelcomeChannel(@NotNull SlashCommandEvent event, @NotNull SlashCommandInteractionEvent e,
                                  @NotNull BlackGuild guild) {
        var welcomeChannel = e.getOption(SET_WELCOME_CHANNEL, OptionMapping::getChannelType);

        if (Objects.requireNonNull(welcomeChannel).isMessage()) {
            var textChannel = e.getOption(SET_WELCOME_CHANNEL, OptionMapping::getAsTextChannel);
            guild.setJoinChannel(textChannel.getIdLong());
            event.send("welcomechannelset");
        } else {
            event.send("nottextchannel");
        }
    }

    public void changeWelcomeChannel(@NotNull SlashCommandEvent event, @NotNull SlashCommandInteractionEvent e,
                                     @NotNull BlackGuild guild) {
        var welcomeChannel = e.getOption(CHANGE_WELCOME_CHANNEL, OptionMapping::getChannelType);

        if (Objects.requireNonNull(welcomeChannel).isMessage()) {
            var textChannel = e.getOption(CHANGE_WELCOME_CHANNEL, OptionMapping::getAsTextChannel);
            guild.setJoinChannel(-1);
            guild.setJoinChannel(textChannel.getIdLong());
            event.send("welcomechannelchanged");
        } else {
            event.send("nottextchannel");
        }
    }

    public void removeWelcomeChannel(@NotNull SlashCommandEvent event, SlashCommandInteractionEvent e,
                                     @NotNull BlackGuild guild) {
        guild.setJoinChannel(-1);
        event.send("welcomechannelremoved");
    }
}
