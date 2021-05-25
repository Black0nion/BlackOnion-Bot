package com.github.black0nion.blackonionbot.commands.bot;

import java.time.Duration;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.CommandVisibility;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.utils.ValueManager;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ActivityCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "activity" };
	}
	
	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
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
			message.reply(EmbedUtils.getErrorEmbed(author, guild).addField(LanguageSystem.getTranslation("wrongargument", author, guild), Utils.getPleaseUse(guild, author, this), false).build()).delay(Duration.ofSeconds(3)).flatMap(Message::delete).queue();
			return;
		}
		message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField("newactivity", args[1] + " " + status, false).build()).delay(Duration.ofSeconds(3)).flatMap(Message::delete).queue();
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
}