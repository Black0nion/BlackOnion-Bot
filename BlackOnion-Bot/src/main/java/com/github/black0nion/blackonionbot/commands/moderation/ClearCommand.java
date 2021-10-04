package com.github.black0nion.blackonionbot.commands.moderation;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class ClearCommand extends SlashCommand {

    public ClearCommand() {
	this.setData(new CommandData("clear", "Clears a specific amount of messages")
		.addOption(OptionType.INTEGER, "messagecount", "The amount of messages to delete", true))
	.setRequiredPermissions(Permission.MESSAGE_MANAGE).setRequiredBotPermissions(Permission.MESSAGE_MANAGE);
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	try {
	    final long amountLong = e.getOptionsByType(OptionType.INTEGER).get(0).getAsLong();
	    if (amountLong < 2 || amountLong > 100) {
		cmde.errorPrivate("wrongargument", "numberofdeletedmessages");
		return;
	    }
	    final int amount = (int) amountLong;

	    try {
		channel.getIterableHistory().cache(false).queue(msgs -> {
		    final OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC).minusWeeks(2).plusSeconds(1);
		    final List<Message> messages = new ArrayList<>();
		    int i = amount + 1;
		    for (final Message m : msgs) {
			if (!m.isPinned() && m.getTimeCreated().isAfter(now)) {
			    messages.add(m);
			}
			if (--i <= 0) {
			    break;
			}
		    }

		    if (messages.size() < 2 || messages.size() > 100) {
			cmde.errorPrivate("wrongargument", "nomessagesfound");
			return;
		    }
		    channel.deleteMessages(messages).queue(succ -> {
			if (messages.size() != amountLong) {
			    cmde.success("messagesdeleted", cmde.getTranslation("msgsgotdeletedless", new Placeholder("msgcount", messages.size()), new Placeholder("remaining", amount - messages.size())));
			} else {
			    cmde.successPrivate("messagesdeleted", cmde.getTranslation("msgsgotdeleted", new Placeholder("msgcount", amount)));
			}
		    }, error -> {
		    });
		});
	    } catch (final Exception ex) {
		if (!(ex instanceof IllegalArgumentException)) {
		    ex.printStackTrace();
		    cmde.privateException();
		} else {
		    cmde.errorPrivate("tooold", "messagestooold");
		}
		return;
	    }
	} catch (final Exception ignored) {
	    cmde.sendPleaseUse();
	}
	return;
    }
}