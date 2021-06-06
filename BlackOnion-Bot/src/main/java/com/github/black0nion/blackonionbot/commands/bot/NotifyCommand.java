package com.github.black0nion.blackonionbot.commands.bot;

import java.util.ArrayList;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.misc.Progress;
import com.github.black0nion.blackonionbot.utils.ValueManager;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class NotifyCommand extends Command {
	
	public NotifyCommand() {
		this.setCommand("notify")
			.setProgress(Progress.PAUSED);
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final BlackMessage message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		if (Bot.notifyStatusUsers.contains(author.getId())) {
			Bot.notifyStatusUsers.remove(author.getId());
			final ArrayList<String> users = new ArrayList<String>();
			users.addAll(ValueManager.getArrayAsList("notifyUsers"));
			users.remove(author.getId());
			ValueManager.save("notifyUsers", users);
			Bot.notifyStatusUsers.remove(author.getId());
		} else {
			final ArrayList<String> users = new ArrayList<String>();
			users.addAll(ValueManager.getArrayAsList("notifyUsers"));
			users.add(author.getId());
			ValueManager.save("notifyUsers", users);
			Bot.notifyStatusUsers.add(author.getId());
		}
		cmde.error("Dieses Feature ist derzeit leider nicht verfügbar!", "Discord erlaubt dies seit der neuesten API Version nicht mehr :(");
	}
}