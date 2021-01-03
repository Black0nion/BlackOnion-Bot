package com.github.ahitm_2020_2025.blackonionbot.commands.bot;

import java.util.ArrayList;

import com.github.ahitm_2020_2025.blackonionbot.bot.Bot;
import com.github.ahitm_2020_2025.blackonionbot.enums.Category;
import com.github.ahitm_2020_2025.blackonionbot.enums.Progress;
import com.github.ahitm_2020_2025.blackonionbot.oldcommands.Command;
import com.github.ahitm_2020_2025.blackonionbot.utils.ValueManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class NotifyCommand implements Command {

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, MessageChannel channel) {
		if (Bot.notifyStatusUsers.contains(author.getId())) {
			Bot.notifyStatusUsers.remove(author.getId());
			ArrayList<String> users = new ArrayList<String>();
			users.addAll(ValueManager.getArrayAsList("notifyUsers"));
			users.remove(author.getId());
			ValueManager.save("notifyUsers", users);
			Bot.notifyStatusUsers.remove(author.getId());
			channel.sendMessage("Du wirst jetzt nicht mehr benachrichtigt, wenn der Bot startet und stoppt.").queue();
		} else {
			ArrayList<String> users = new ArrayList<String>();
			users.addAll(ValueManager.getArrayAsList("notifyUsers"));
			users.add(author.getId());
			ValueManager.save("notifyUsers", users);
			Bot.notifyStatusUsers.add(author.getId());
			channel.sendMessage("Du wirst jetzt benachrichtigt, wenn der Bot startet und stoppt.").queue();
		}
	}

	@Override
	public String getDescription() {
		return "Der Bot schreibt dich per DM an, wenn er startet und stoppt.";
	}

	@Override
	public Category getCategory() {
		return Category.BOT;
	}

	@Override
	public Progress getProgress() {
		return Progress.PAUSED;
	}
	
	@Override
	public String[] getCommand() {
		return new String[] {"notify"};
	}
}
