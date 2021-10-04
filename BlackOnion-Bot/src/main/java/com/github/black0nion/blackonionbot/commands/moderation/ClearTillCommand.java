package com.github.black0nion.blackonionbot.commands.moderation;

import java.time.Duration;
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
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class ClearTillCommand extends SlashCommand {

    public ClearTillCommand() {
	this.setData(new CommandData("cleartill", "Clears all messages until a specific one").addOption(OptionType.INTEGER, "messageid", "The ID of the message to clear until (exlusive) (max 99 messages can be deleted)", true)).setRequiredPermissions(Permission.MESSAGE_MANAGE).setRequiredBotPermissions(Permission.MESSAGE_MANAGE);
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	try {
	    final long messageid = e.getOptionsByType(OptionType.INTEGER).get(0).getAsLong();
	    channel.retrieveMessageById(messageid).queue(msg -> {
		try {
		    channel.getHistoryAfter(msg, 100).queue(msgs -> {
			final int msgsize = msgs.size();
			if (msgsize == 0 || msgsize >= 100) {
			    cmde.error("toomanymessages", "toomanymessagesinfo", new Placeholder("msgcount", msgsize));
			    return;
			}
			final OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC).minusWeeks(2).plusSeconds(1);
			final List<Message> messages = new ArrayList<>();
			int i = msgsize + 1;
			for (final Message m : msgs.getRetrievedHistory()) {
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
			    if (messages.size() != msgsize) {
				e.replyEmbeds(cmde.success().addField(cmde.getTranslation("messagesdeleted"), cmde.getTranslation("msgsgotdeletedless", new Placeholder("msgcount", messages.size()), new Placeholder("remaining", msgsize - messages.size())), false).build()).delay(Duration.ofSeconds(5)).flatMap(InteractionHook::deleteOriginal).queue();
			    } else {
				e.replyEmbeds(cmde.success().addField(cmde.getTranslation("messagesdeleted"), cmde.getTranslation("msgsgotdeleted", new Placeholder("msgcount", msgsize)), false).build()).delay(Duration.ofSeconds(5)).flatMap(InteractionHook::deleteOriginal).queue();
			    }
			}, error -> {
			});
		    });
		} catch (final Exception ex) {
		    if (!(ex instanceof IllegalArgumentException)) {
			ex.printStackTrace();
			cmde.privateException();
		    } else {
			ex.printStackTrace();
			cmde.errorPrivate("tooold", "messagestooold");
		    }
		    return;
		}
	    }, err -> {
		cmde.error("nomessagesfound", "pleaseinputmessage");
		return;
	    });
	} catch (final Exception ignored) {
	    cmde.sendPleaseUse();
	}
	return;
    }
}