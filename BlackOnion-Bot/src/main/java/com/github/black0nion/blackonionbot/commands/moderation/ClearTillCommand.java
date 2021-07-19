package com.github.black0nion.blackonionbot.commands.moderation;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ClearTillCommand extends Command {

    public ClearTillCommand() {
	this.setCommand("cleartill", "clearuntil").setSyntax("<MessageID>").setRequiredArgumentCount(1).setRequiredPermissions(Permission.MESSAGE_MANAGE).setRequiredBotPermissions(Permission.MESSAGE_MANAGE);
    }

    @Override
    public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final BlackMessage message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	try {
	    if (!Utils.isLong(args[1])) {
		cmde.sendPleaseUse();
		return;
	    }
	    final long messageid = Long.parseLong(args[1]);
	    channel.retrieveMessageById(messageid).queue(msg -> {
		try {
		    channel.getHistoryAfter(msg, 100).queue(msgs -> {
			final int msgsize = msgs.size();
			if (msgsize == 0 || msgs.getMessageById(message.getIdLong()) == null) {
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
			    message.delete().queue();
			    message.reply(EmbedUtils.getErrorEmbed(author, guild).addField(cmde.getTranslation("wrongargument"), cmde.getTranslation("nomessagesfound"), false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
			    return;
			}
			channel.deleteMessages(messages).queue(succ -> {
			    if (messages.size() != msgsize) {
				message.reply(cmde.success().addField(cmde.getTranslation("messagesdeleted"), cmde.getTranslation("msgsgotdeletedless", new Placeholder("msgcount", messages.size()), new Placeholder("remaining", msgsize - messages.size())), false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
			    } else {
				message.reply(cmde.success().addField(cmde.getTranslation("messagesdeleted"), cmde.getTranslation("msgsgotdeleted", new Placeholder("msgcount", msgsize)), false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
			    }
			}, error -> {
			});
		    });
		} catch (final Exception ex) {
		    if (!(ex instanceof IllegalArgumentException)) {
			ex.printStackTrace();
			cmde.selfDestructingException();
		    } else {
			ex.printStackTrace();
			cmde.error("tooold", "messagestooold", newmsg -> newmsg.delete().queueAfter(5, TimeUnit.SECONDS));
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