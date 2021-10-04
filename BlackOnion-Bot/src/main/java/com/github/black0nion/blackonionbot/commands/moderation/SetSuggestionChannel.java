package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import net.dv8tion.jda.api.entities.Message;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class SetSuggestionChannel extends SlashCommand {

    public SetSuggestionChannel() {
        this.setData(new CommandData("setsuggestionschannel", "Set the channel where suggestions will be sent in"))
            .setRequiredPermissions(Permission.MESSAGE_MANAGE)
            .setRequiredBotPermissions(Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION);
    }

    @Override
    public void execute(SlashCommandExecutedEvent cmde, SlashCommandEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
        cmde.successPrivate("suggestionchannelset", "thisissuggestionchannel");
        guild.setSuggestionsChannel(channel.getIdLong());
    }
}