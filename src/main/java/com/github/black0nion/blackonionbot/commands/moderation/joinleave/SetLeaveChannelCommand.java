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

public class SetLeaveChannelCommand extends SlashCommand {
    private static final String SET_LEAVE_COMMAND = "set_leave_channel";
    private static final String CHANGE_LEAVE_COMMAND = "change_leave_channel";
    private static final String REMOVE_LEAVE_COMMAND = "remove_leave_channel";
    private static final String SET_LEAVE_CHANNEL = "channel";
    private static final String CHANGE_LEAVE_CHANNEL = "channel";

    public SetLeaveChannelCommand() {
        super(builder(Commands.slash("leave_channel", "Used to to set/remove/change the leave channel for the guild.")
                .addSubcommands(
                        new SubcommandData(SET_LEAVE_COMMAND, "Used to set the leave channel for the guild.").addOption(
                                OptionType.CHANNEL, SET_LEAVE_CHANNEL, "The channel to set as the leave channel.",
                                true),
                        new SubcommandData(CHANGE_LEAVE_COMMAND, "Used to change the leave channel for the guild.")
                                .addOption(OptionType.CHANNEL, CHANGE_LEAVE_CHANNEL,
                                        "The channel to change the leave channel to.", true),
                        new SubcommandData(REMOVE_LEAVE_COMMAND, "Used to remove the leave channel for the guild.")))
                .setRequiredBotPermissions(Permission.MESSAGE_MANAGE)
                .setRequiredPermissions(Permission.MANAGE_SERVER));
    }

    @Override
    public void execute(@NotNull SlashCommandEvent event, @NotNull SlashCommandInteractionEvent e, BlackMember member,
                        BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
        switch (Objects.requireNonNull(e.getSubcommandName())) {
            case SET_LEAVE_COMMAND -> setLeaveChannel(event, e, guild);
            case CHANGE_LEAVE_COMMAND -> changeLeaveChannel(event, e, guild);
            case REMOVE_LEAVE_COMMAND -> removeLeaveChannel(event, e, guild);
            default -> event.send("invalidsubcommand");
        }
    }

    public void setLeaveChannel(@NotNull SlashCommandEvent event, @NotNull SlashCommandInteractionEvent e,
                                @NotNull BlackGuild guild) {
        var leaveChannel = e.getOption(SET_LEAVE_CHANNEL, OptionMapping::getChannelType);

        if (Objects.requireNonNull(leaveChannel).isMessage()) {
            var textChannel = e.getOption(SET_LEAVE_CHANNEL, OptionMapping::getAsTextChannel);
            guild.setLeaveChannel(textChannel.getIdLong());
            event.send("leavechannelset");
        } else {
            event.send("nottextchannel");
        }
    }

    public void changeLeaveChannel(@NotNull SlashCommandEvent event, @NotNull SlashCommandInteractionEvent e,
                                   @NotNull BlackGuild guild) {
        var newLeaveChannel = e.getOption(CHANGE_LEAVE_CHANNEL, OptionMapping::getChannelType);

        if (Objects.requireNonNull(newLeaveChannel).isMessage()) {
            var textChannel = e.getOption(CHANGE_LEAVE_CHANNEL, OptionMapping::getAsTextChannel);
            guild.setLeaveChannel(-1);
            guild.setLeaveChannel(textChannel.getIdLong());
            event.send("leavechannelchanged");
        } else {
            event.send("nottextchannel");
        }
    }

    public void removeLeaveChannel(@NotNull SlashCommandEvent event, SlashCommandInteractionEvent e,
                                   @NotNull BlackGuild guild) {
        guild.setLeaveChannel(-1);
        event.send("leavechannelcleared");
    }
}