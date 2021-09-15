package com.github.black0nion.blackonionbot.commands.moderation;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ClearCommand extends Command {

    public ClearCommand() {
	this.setCommand("clear").setSyntax("<message count>").setRequiredArgumentCount(1).setRequiredPermissions(Permission.MESSAGE_MANAGE).setRequiredBotPermissions(Permission.MESSAGE_MANAGE);
    }

    @Override
    public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	try {
	    final int amount = Integer.parseInt(args[1]);
	    if (amount < 2 || amount > 100) {
		message.replyEmbeds(EmbedUtils.getErrorEmbed(author, guild).addField(cmde.getTranslation("wrongargument"), cmde.getTranslation("numberofdeletedmessages"), false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
		return;
	    }

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
			message.delete().queue();
			message.replyEmbeds(EmbedUtils.getErrorEmbed(author, guild).addField(cmde.getTranslation("wrongargument"), cmde.getTranslation("nomessagesfound"), false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
			return;
		    }
		    channel.deleteMessages(messages).queue(succ -> {
			if (messages.size() != amount) {
			    message.replyEmbeds(cmde.success().addField(cmde.getTranslation("messagesdeleted"), cmde.getTranslation("msgsgotdeletedless", new Placeholder("msgcount", messages.size()), new Placeholder("remaining", amount - messages.size())), false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
			} else {
			    message.replyEmbeds(cmde.success().addField(cmde.getTranslation("messagesdeleted"), cmde.getTranslation("msgsgotdeleted", new Placeholder("msgcount", amount)), false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
			}
		    }, error -> {
		    });
		});
	    } catch (final Exception ex) {
		if (!(ex instanceof IllegalArgumentException)) {
		    ex.printStackTrace();
		    cmde.selfDestructingException();
		} else {
		    cmde.error("tooold", "messagestooold", msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
		}
		return;
	    }
	} catch (final Exception ignored) {
	    cmde.sendPleaseUse();
	}
	return;
    }
}