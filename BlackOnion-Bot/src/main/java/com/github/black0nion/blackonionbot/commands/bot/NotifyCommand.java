package com.github.black0nion.blackonionbot.commands.bot;

import java.util.ArrayList;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.Progress;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.ValueManager;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class NotifyCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "notify" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		if (Bot.notifyStatusUsers.contains(author.getId())) {
			Bot.notifyStatusUsers.remove(author.getId());
			ArrayList<String> users = new ArrayList<String>();
			users.addAll(ValueManager.getArrayAsList("notifyUsers"));
			users.remove(author.getId());
			ValueManager.save("notifyUsers", users);
			Bot.notifyStatusUsers.remove(author.getId());
			message.reply(EmbedUtils.getDefaultErrorEmbed(author).addField("Dieses Feature ist derzeit leider nicht verf�gbar!", "Discord erlaubt dies seit der neuesten API Version nicht mehr :(", false).build()).queue();
		} else {
			ArrayList<String> users = new ArrayList<String>();
			users.addAll(ValueManager.getArrayAsList("notifyUsers"));
			users.add(author.getId());
			ValueManager.save("notifyUsers", users);
			Bot.notifyStatusUsers.add(author.getId());
			message.reply(EmbedUtils.getDefaultErrorEmbed(author).addField("Dieses Feature ist derzeit leider nicht verf�gbar!", "Discord erlaubt dies seit der neuesten API Version nicht mehr :(", false).build()).queue();
		}
	}
	
	@Override
	public Category getCategory() {
		return Category.BOT;
	}

	@Override
	public Progress getProgress() {
		return Progress.PAUSED;
	}
}