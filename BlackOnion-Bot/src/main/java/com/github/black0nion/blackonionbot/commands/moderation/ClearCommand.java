package com.github.black0nion.blackonionbot.commands.moderation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ClearCommand implements Command {

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		final TextChannel textChannel = e.getChannel();
		if (member.hasPermission(textChannel, Permission.MESSAGE_MANAGE) ) {
			if (args.length == 2) {
				try {
					int amount = Integer.parseInt(args[1]);
					textChannel.deleteMessages(get(channel, amount)).queue();
					channel.sendMessage(EmbedUtils.getDefaultSuccessEmbed(author, guild).addField(LanguageSystem.getTranslation("messagesdeleted", author, guild), amount + " " + LanguageSystem.getTranslation("msgsgotdeleted", author, guild), false).build()).complete().delete().queueAfter(3, TimeUnit.SECONDS);
				} catch (NumberFormatException ex) {
					channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author, guild).addField(LanguageSystem.getTranslation("wrongargument", author, guild), LanguageSystem.getTranslation("numberofdeletedmessages", author, guild), false).build()).complete().delete().queueAfter(3, TimeUnit.SECONDS);
					return;
				}
				return;
			} else {
				channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author, guild).addField(LanguageSystem.getTranslation("wrongargument", author, guild), LanguageSystem.getTranslation("numberofdeletedmessages", author, guild), false).build()).complete().delete().queueAfter(3, TimeUnit.SECONDS);
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
	public Permission[] getRequiredPermissions() {
		return new Permission[] {Permission.MESSAGE_MANAGE};
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
