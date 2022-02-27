package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SetSuggestionChannel extends Command {

	public SetSuggestionChannel() {
		this.setCommand("setsuggestionchannel", "setsuggestionschannel", "setsuggestchannel")
			.setRequiredPermissions(Permission.MESSAGE_MANAGE)
			.setRequiredBotPermissions(Permission.MESSAGE_SEND, Permission.MESSAGE_ADD_REACTION);
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		cmde.success("suggestionchannelset", "thisissuggestionchannel");
		guild.setSuggestionsChannel(channel.getIdLong());
	}
}