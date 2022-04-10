package com.github.black0nion.blackonionbot.commands.misc;

import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.misc.CustomPermission;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class SetPollChannelCommand extends SlashCommand {
    private static final String SET_POLL_CHANNEL_COMMAND = "setpollchannel";
    private static final String SET_POLL_CHANNEL = "channel";
    private static final String REMOVE_POLL_CHANNEL_COMMAND = "removepollchannel";
    private static final String UPDATE_POLL_CHANNEL_COMMAND = "updatepollchannel";
    private static final String UPDATE_POLL_CHANNEL = "channel";

    protected SetPollChannelCommand() {
        super(builder(Commands.slash("pollchannel", "Used to set/update/remove the channel where the poll will be posted.")
                .addSubcommands(
                        new SubcommandData(SET_POLL_CHANNEL_COMMAND, "Used to set the channel where the poll will be posted.")
                                .addOption(OptionType.CHANNEL, SET_POLL_CHANNEL, "The channel where the poll will be posted.", true),
                        new SubcommandData(REMOVE_POLL_CHANNEL_COMMAND, "Used to remove the channel where the poll will be posted."),
                        new SubcommandData(UPDATE_POLL_CHANNEL_COMMAND, "Used to update the channel where the poll will be posted.")
                                .addOption(OptionType.CHANNEL, UPDATE_POLL_CHANNEL, "The channel where the poll will be posted.", true)))
                .setRequiredCustomPermissions(CustomPermission.ADMIN));
    }

    @Override
    public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
        switch (e.getSubcommandName()) {
            case SET_POLL_CHANNEL_COMMAND -> setPollChannel(cmde, e, member, author, guild, channel);
            case REMOVE_POLL_CHANNEL_COMMAND -> removePollChannel(cmde, e, member, author, guild, channel);
            case UPDATE_POLL_CHANNEL_COMMAND -> updatePollChannel(cmde, e, member, author, guild, channel);
            default -> e.getChannel().sendMessage("Invalid subcommand.").queue();
        }
    }

    public void setPollChannel(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
        var pollChannel = e.getOption(SET_POLL_CHANNEL, OptionMapping::getChannelType);
        if(pollChannel.isMessage()) {
            var pollChannelAsTextChannel = e.getOption(SET_POLL_CHANNEL, OptionMapping::getAsTextChannel);
            guild.setPollChannel(pollChannelAsTextChannel.getIdLong());
            cmde.send("pollchannelset");
        } else {
            cmde.send("nottextchannel");
        }
    }

    public void removePollChannel(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
        guild.setPollChannel(-1);
        cmde.send("pollchannelremoved");
    }

    public void updatePollChannel(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
        var pollChannel = e.getOption(UPDATE_POLL_CHANNEL, OptionMapping::getChannelType);
        if(pollChannel.isMessage()) {
            var pollChannelAsTextChannel = e.getOption(UPDATE_POLL_CHANNEL, OptionMapping::getAsTextChannel);
            guild.setPollChannel(-1);
            guild.setPollChannel(pollChannelAsTextChannel.getIdLong());
            cmde.send("pollchannelupdated");
        } else {
            cmde.send("nottextchannel");
        }
    }
}
