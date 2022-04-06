/**
 *
 */
package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.commands.TextCommand;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SuggestCommand extends TextCommand {

	public SuggestCommand() {
		this.setCommand("suggest").setSyntax("<suggestion>").setRequiredArgumentCount(1);
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e,
			final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild,
			final TextChannel channel) {
		final long suggestionsChannelId = guild.getSuggestionsChannel();
		if (suggestionsChannelId == -1) {
			cmde.error("suggestionsoff", "nosuggestionfeature");
		} else {
			final TextChannel suggestionsChannel = guild.getTextChannelById(suggestionsChannelId);
			if (suggestionsChannel == null) {
				cmde.error("suggestionschannelnotfound", "pleaseresetsuggestionschannel");
			} else if (!(guild.getSelfMember().hasPermission(suggestionsChannel, Permission.MESSAGE_SEND,
					Permission.MESSAGE_ADD_REACTION))) {
				channel.sendMessageEmbeds(Utils.noRights(guild, guild.getSelfBlackMember().getBlackUser(),
						Permission.MESSAGE_SEND, Permission.MESSAGE_ADD_REACTION)).queue();
			} else {
				// all good, we can send the suggestion
				suggestionsChannel.sendMessageEmbeds(cmde.success().setTitle("suggestion")
						.setDescription(String.join(" ", Utils.removeFirstArg(args))).build()).queue(msg -> {
							msg.addReaction("U+1F44D").queue();
							msg.addReaction("U+1F44E").queue();
						});
				cmde.success("suggestiongotsent", "suggestionisin",
						new Placeholder("channel", suggestionsChannel.getAsMention()));
			}
		}
	}
}
