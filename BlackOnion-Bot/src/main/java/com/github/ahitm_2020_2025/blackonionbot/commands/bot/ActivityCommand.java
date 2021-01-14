package com.github.ahitm_2020_2025.blackonionbot.commands.bot;

import java.util.concurrent.TimeUnit;

import com.github.ahitm_2020_2025.blackonionbot.enums.Category;
import com.github.ahitm_2020_2025.blackonionbot.enums.CommandVisibility;
import com.github.ahitm_2020_2025.blackonionbot.oldcommands.Command;
import com.github.ahitm_2020_2025.blackonionbot.systems.language.LanguageSystem;
import com.github.ahitm_2020_2025.blackonionbot.utils.EmbedUtils;
import com.github.ahitm_2020_2025.blackonionbot.utils.ValueManager;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ActivityCommand implements Command {
	
	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		String status = "";
		for (int i = 2; i <= args.length - 1; i++) {
			status += args[i] + (i == args.length - 1 ? "" : " ");
		}
		if(args[1].toLowerCase().contains("playing")) {
			e.getJDA().getPresence().setActivity(Activity.playing(status));
		} else if (args[1].toLowerCase().contains("watching")) {
			e.getJDA().getPresence().setActivity(Activity.watching(status));
		} else if (args[1].toLowerCase().contains("listening")) {
			e.getJDA().getPresence().setActivity(Activity.listening(status));
		} else {
			channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author).addField(LanguageSystem.getTranslatedString("wrongargument", author, guild), LanguageSystem.getTranslatedString("pleaseuse", author, guild) + " ``playing``, ``watching`` " + LanguageSystem.getTranslatedString("or", author, guild) + " ``listening``!", false).build()).complete().delete().queueAfter(3, TimeUnit.SECONDS);
			return;
		}
		channel.sendMessage(EmbedUtils.getDefaultSuccessEmbed(author).addField(LanguageSystem.getTranslatedString("newactivity", author, e.getGuild()), args[1] + " " + status, false).build()).complete().delete().queueAfter(3, TimeUnit.SECONDS);
		ValueManager.save("activityType", args[1]);
		ValueManager.save("activity", status);
	}

	@Override
	public String getSyntax() {
		return "[playing | watching | listening] <Text>";
	}
	
	@Override
	public int getRequiredArgumentCount() {
		return 2;
	}
	
	@Override
	public boolean requiresBotAdmin() {
		return true;
	}
	
	@Override
	public CommandVisibility getVisisbility() {
		return CommandVisibility.HIDDEN;
	}
	
	public static Activity getActivity() {
		return getActivity(ValueManager.getString("activityType"), ValueManager.getString("activity"));
	}

	public static Activity getActivity(String status, String activity) {
		if(status.contains("playing")) {
			return Activity.playing(activity);
		} else if (status.contains("watching")) {
			return Activity.watching(activity);
		} else if (status.contains("listening")) {
			return Activity.listening(activity);
		} else {
			return null;
		}
	}
	
	@Override
	public Category getCategory() {
		return Category.BOT;
	}

	@Override
	public String[] getCommand() {
		return new String[] {"activity"};
	}
}