package com.github.black0nion.blackonionbot.commands.moderation;

import java.time.Duration;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class SetJoinLeaveChannelCommand extends SlashCommand {

    public SetJoinLeaveChannelCommand() {
        OptionData option = new OptionData(OptionType.STRING, "type", "The type of the channel to set", true)
                .addChoice("Join", "join").addChoice("Leave", "leave");
        this.setData(
                new CommandData("setjoinleavechannel", "Set the channel where members leaving or joining should be announced")
                        .addSubcommands(
                                new SubcommandData("set", "Set to a specific channel").addOptions(option),
                                new SubcommandData("clear", "Clear a existing channel").addOptions(option),
                                new SubcommandData("info", "Shows info about a existing channel setting").addOptions(option)
                        ))
        .setRequiredBotPermissions(Permission.MESSAGE_MANAGE).setRequiredPermissions(Permission.MANAGE_SERVER);
    }

    @Override
    public void execute(SlashCommandExecutedEvent cmde, SlashCommandEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
        String action = e.getSubcommandName();
        boolean isSet = action.equals("set");
        boolean isJoin = e.getOptionsByType(OptionType.STRING).get(0).getAsString().equals("join");
        if (action.equals("info")) {
            e.reply("<#" + (isJoin ? guild.getJoinChannel() : guild.getLeaveChannel()) + ">").queue();
        } else {
            if (isJoin) guild.setJoinChannel(isSet ? channel.getIdLong() : -1);
            else guild.setJoinChannel(isSet ? channel.getIdLong() : -1);
            String s = (isJoin ? "welcome" : "leave") + "channel" + (isSet ? "set" : "cleared");
            cmde.success(s, s + "info");
        }
    }
}