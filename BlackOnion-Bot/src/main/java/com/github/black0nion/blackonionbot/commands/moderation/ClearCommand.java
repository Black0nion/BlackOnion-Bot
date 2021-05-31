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
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ClearCommand extends Command {
	
	public ClearCommand() {
		this.setCommand("clear")
			.setSyntax("<message count>")
			.setRequiredArgumentCount(1)
			.setRequiredPermissions(Permission.MESSAGE_MANAGE)
			.setRequiredBotPermissions(Permission.MESSAGE_MANAGE);
	}

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {		
		try {
			final int amount = Integer.parseInt(args[1]);
			if (amount < 2 || amount > 100) {
				message.reply(EmbedUtils.getErrorEmbed(author, guild).addField(LanguageSystem.getTranslation("wrongargument", author, guild), LanguageSystem.getTranslation("numberofdeletedmessages", author, guild), false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
				return;
			}
			
			try {
				channel.getIterableHistory().cache(false).queue(msgs -> {						
					final OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC).minusWeeks(2).plusSeconds(1);
					List<Message> messages = new ArrayList<>();
					int i = amount + 1;
					for (Message m : msgs) {
						if (!m.isPinned() && m.getTimeCreated().isAfter(now)) {
							messages.add(m);
						}
						if (--i <= 0) break;
					}
					
					if (messages.size() < 2 || messages.size() > 100) {
						message.reply(EmbedUtils.getErrorEmbed(author, guild).addField(LanguageSystem.getTranslation("wrongargument", author, guild), LanguageSystem.getTranslation("nomessagesfound", author, guild), false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
						return;
					}
					channel.deleteMessages(messages).queue(succ -> {
						// TODO: delete after x seconds
						if (messages.size() != amount) {
							message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField(LanguageSystem.getTranslation("messagesdeleted", author, guild), LanguageSystem.getTranslation("msgsgotdeletedless", author, guild).replace("%msgcount%", String.valueOf(messages.size())).replace("%remaining%", String.valueOf(messages.size() - amount)), false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
						} else
							message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField(LanguageSystem.getTranslation("messagesdeleted", author, guild), LanguageSystem.getTranslation("msgsgotdeleted", author, guild).replace("%msgcount%", String.valueOf(amount)), false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
					}, error -> { });
				});
			} catch (Exception ex) {
				if (!(ex instanceof IllegalArgumentException)) {					
					ex.printStackTrace();
					cmde.selfDestructingException();
				} else {
					cmde.error("tooold", "messagestooold", msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
				}
				return;
			}
		} catch (Exception ignored) { cmde.sendPleaseUse(); }
		return;
	}
}