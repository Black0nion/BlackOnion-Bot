package com.github.ahitm_2020_2025.blackonionbot.commands.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.ahitm_2020_2025.blackonionbot.Command;
import com.github.ahitm_2020_2025.blackonionbot.enums.Category;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ClearCommand implements Command {

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, MessageChannel channel) {
		if (member.hasPermission(e.getTextChannel(), Permission.MESSAGE_MANAGE) ) {
			if (args.length == 2) {
				try {
					int amount = Integer.parseInt(args[1]);
					e.getTextChannel().deleteMessages(get(channel, amount)).queue();
					e.getTextChannel().sendMessage(amount + " Nachrichten gelöscht.").complete().delete().queueAfter(3, TimeUnit.SECONDS);
				} catch (NumberFormatException ex) {
					channel.sendMessage("Bitte gebe eine gültige Zahl ein!").complete().delete().queueAfter(3, TimeUnit.SECONDS);
					return;
				}
				return;
			} else {
				channel.sendMessage("Bitte gebe die Anzahl an zu löschenden Nachrichten an!").complete().delete().queueAfter(3, TimeUnit.SECONDS);
				return;
			}
		}
	}
	
	public List<Message> get(MessageChannel channel, int amount) {
		List<Message> messages = new ArrayList<>();
		int i = amount + 1;
		
		for (Message m : channel.getIterableHistory().cache(false)) {
			if (!m.isPinned()) {
				messages.add(m);
			}
			if (--i <= 0) break;
		}
		
		return messages;
	}

	@Override
	public String getDescription() {
		return "Löscht eine bestimme Anzahl an Nachrichten";
	}

	@Override
	public String getSyntax() {
		return "<message count>";
	}
	
	@Override
	public Category getCategory() {
		return Category.MODERATION;
	}

	@Override
	public String[] getCommand() {
		return new String[] {"clear"};
	}

}
