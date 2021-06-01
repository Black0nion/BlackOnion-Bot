/**
 * 
 */
package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * @author _SIM_
 *
 */
public class SuggestCommand extends Command {

	public SuggestCommand() {
		this.setCommand("suggest")
			.setSyntax("<suggestion>")
			.setRequiredArgumentCount(1);
	}

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		// TODO: translate
		final long suggestionsChannelId = guild.getSuggestionsChannel();
		if (suggestionsChannelId == -1) {
			cmde.error("suggestionsoff", "nosuggestionfeature");
			return;
		} else {
			TextChannel suggestionsChannel = guild.getTextChannelById(suggestionsChannelId);
			if (suggestionsChannel == null) {
				cmde.error("suggestionschannelnotfound", "pleaseresetsuggestionschannel");
				return;
			} else if (!(guild.getSelfMember().hasPermission(suggestionsChannel, Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION))) {
				Utils.noRights(guild, guild.getSelfBlackMember().getBlackUser(), Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION);
			} else {
				// all good, we can send the suggestion
				suggestionsChannel.sendMessage(cmde.success().setTitle("suggestion").setDescription(String.join(" ", Utils.removeFirstArg(args))).build()).queue(msg -> {
					msg.addReaction("U+1F44D").queue();
					msg.addReaction("U+1F44E").queue();
				});
				cmde.success("suggestiongotsent", "suggestionisin", new Placeholder("channel", suggestionsChannel.getAsMention()));
			}
		}
	}
}