package com.github.black0nion.blackonionbot.commands.moderation;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ClearCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "clear" };
	}

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		try {
			int amount = Integer.parseInt(args[1]);
			channel.deleteMessages(get(channel, amount)).queue();
			message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField(LanguageSystem.getTranslation("messagesdeleted", author, guild), amount + " " + LanguageSystem.getTranslation("msgsgotdeleted", author, guild), false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
		} catch (Exception ex) {
			ex.printStackTrace();
			message.reply(EmbedUtils.getErrorEmbed(author, guild).addField(LanguageSystem.getTranslation("wrongargument", author, guild), LanguageSystem.getTranslation("numberofdeletedmessages", author, guild), false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
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
	public int getRequiredArgumentCount() {
		return 1;
	}
}