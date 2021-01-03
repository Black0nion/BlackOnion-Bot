package com.github.ahitm_2020_2025.blackonionbot.commands.bot;

import com.github.ahitm_2020_2025.blackonionbot.bot.BotInformation;
import com.github.ahitm_2020_2025.blackonionbot.enums.Category;
import com.github.ahitm_2020_2025.blackonionbot.enums.CommandVisisbility;
import com.github.ahitm_2020_2025.blackonionbot.oldcommands.Command;
import com.github.ahitm_2020_2025.blackonionbot.utils.ValueManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ActivityCommand implements Command {
	
	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, MessageChannel channel) {
		if (!member.hasPermission(Permission.MANAGE_SERVER)) {
			channel.sendMessage("Du darfst das nicht!").queue();
		} else if (args.length >= 3) {	
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
				channel.sendMessage("Bitte gebe ``playing``, ``watching`` oder ``listening`` ein!").queue();
				return;
			}
			channel.sendMessage("Der Status wurde auf " + args[1] + " " + status + " gesetzt!\nACHTUNG: Es könnte eine Zeit dauern, bis der Status von Discord übernommen wurde!").queue();
			ValueManager.save("activityType", args[1]);
			ValueManager.save("activity", status);
		} else {
			channel.sendMessage("Syntax: " + BotInformation.prefix + "activity ``" + getSyntax() + "``").queue();
		}
	}

	@Override
	public String getDescription() {
		return "Setzt die Aktivität des Bots";
	}

	@Override
	public String getSyntax() {
		return "[playing | watching | listening] <Text>";
	}
	
	@Override
	public CommandVisisbility getVisisbility() {
		return CommandVisisbility.HIDDEN;
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