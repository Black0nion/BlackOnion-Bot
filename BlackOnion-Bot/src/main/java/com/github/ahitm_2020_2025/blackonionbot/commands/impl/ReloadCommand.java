package com.github.ahitm_2020_2025.blackonionbot.commands.impl;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.github.ahitm_2020_2025.blackonionbot.BirthdaySystem;
import com.github.ahitm_2020_2025.blackonionbot.Command;
import com.github.ahitm_2020_2025.blackonionbot.DefaultValues;
import com.github.ahitm_2020_2025.blackonionbot.bot.Bot;
import com.github.ahitm_2020_2025.blackonionbot.enums.CommandVisisbility;
import com.github.ahitm_2020_2025.blackonionbot.listeners.HandRaiseSystem;
import com.github.ahitm_2020_2025.blackonionbot.listeners.MessageLogSystem;
import com.github.ahitm_2020_2025.blackonionbot.utils.ValueManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ReloadCommand implements Command {

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, MessageChannel channel) {
		if (!member.hasPermission(Permission.ADMINISTRATOR))
			return;
		message.delete().queue();
		e.getGuild().getTextChannelById(HandRaiseSystem.channelID).addReactionById(HandRaiseSystem.messageID, "🙋").queue();
		channel.sendMessage("Alle Configs wurden reloaded.").complete().delete().queueAfter(3, TimeUnit.SECONDS);
	}
	
	public static void reload() {
		DefaultValues.init();
		MessageLogSystem.init();
		Bot.notifyStatusUsers = new ArrayList<String>(ValueManager.getArrayAsList("notifyUsers"));
		BirthdaySystem.reload();
	}

	@Override
	public String getDescription() {
		return "Reloaded alle Configs";
	}
	
	@Override
	public CommandVisisbility getVisisbility() {
		return CommandVisisbility.HIDDEN;
	}
	
	@Override
	public String[] getCommand() {
		return new String[] {"reload", "rl"};
	}

}
