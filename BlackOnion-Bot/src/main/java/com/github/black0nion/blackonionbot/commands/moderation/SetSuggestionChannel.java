package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SetSuggestionChannel extends Command {
	
	public SetSuggestionChannel() {
		this.setCommand("setsuggestionchannel", "setsuggestionschannel", "setsuggestchannel")
			.setRequiredPermissions(Permission.MESSAGE_MANAGE)
			.setRequiredBotPermissions(Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION);
	}

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		cmde.success("suggestionchannelset", "thisissuggestionchannel");
		guild.setSuggestionsChannel(channel.getIdLong());
	}
}