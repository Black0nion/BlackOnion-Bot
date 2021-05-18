package com.github.black0nion.blackonionbot.commands.moderation;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

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
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, TextChannel channel) {
		try {
			int amount = Integer.parseInt(args[1]);
			channel.deleteMessages(get(channel, amount)).queue();
			channel.sendMessage(EmbedUtils.getDefaultSuccessEmbed(author, guild).addField(LanguageSystem.getTranslatedString("messagesdeleted", author, guild), amount + " " + LanguageSystem.getTranslatedString("msgsgotdeleted", author, guild), false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
		} catch (Exception ex) {
			ex.printStackTrace();
			channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author, guild).addField(LanguageSystem.getTranslatedString("wrongargument", author, guild), LanguageSystem.getTranslatedString("numberofdeletedmessages", author, guild), false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
			return;
		}
		return;
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
		return new Permission[] { Permission.MESSAGE_MANAGE };
	}
	
	@Override
	public Permission[] getRequiredBotPermissions() {
		return new Permission[] { Permission.MESSAGE_MANAGE };
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
	
	@Override
	public int getRequiredArgumentCount() {
		return 1;
	}
}