package com.github.black0nion.blackonionbot.commands.bot;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.CustomPermission;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.utils.ValueManager;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ActivityCommand extends Command {
	
	public ActivityCommand() {
		this.setCommand("activity")
			.setSyntax("([playing | watching | listening] <Text>) | clear")
			.setRequiredArgumentCount(1)
			.setRequiredCustomPermissions(CustomPermission.ADMIN)
			.setHidden()
			.setCategory(Category.BOT);
	}
	
	@Override
	public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final BlackMessage message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		message.delete().queue();
		final String activityType = args[1].toLowerCase();
		
		if (activityType.equalsIgnoreCase("clear")) {
			ValueManager.remove("activityType");
			Bot.restartSwitchingStatus(e.getJDA());
			message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField("activitycleared", "theactivitygotcleared", false).build()).delay(Duration.ofSeconds(3)).flatMap(Message::delete).queue();
			return;
		}
		
		if (args.length < 2) {
			cmde.sendPleaseUse(msg -> msg.delete().queueAfter(3, TimeUnit.SECONDS));
			return;
		}
		
		final String status = String.join(" ", Utils.subArray(args, 2));
		
		if (activityType.contains("playing"))
			e.getJDA().getPresence().setActivity(Activity.playing(status));
		else if (activityType.contains("watching"))
			e.getJDA().getPresence().setActivity(Activity.watching(status));
		else if (activityType.contains("listening"))
			e.getJDA().getPresence().setActivity(Activity.listening(status));
		else {
			message.reply(EmbedUtils.getErrorEmbed(author, guild).addField(LanguageSystem.getTranslation("wrongargument", author, guild), CommandEvent.getPleaseUse(guild, author, this), false).build()).delay(Duration.ofSeconds(3)).flatMap(Message::delete).queue();
			return;
		}
		message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField("newactivity", args[1] + " " + status, false).build()).delay(Duration.ofSeconds(3)).flatMap(Message::delete).queue();
		ValueManager.save("activityType", args[1]);
		ValueManager.save("activity", status);
	}
	
	public static Activity getActivity() {
		return getActivity(ValueManager.getString("activityType"), ValueManager.getString("activity"));
	}

	public static Activity getActivity(final String status, final String activity) {
		if(status.contains("playing"))
			return Activity.playing(activity);
		else if (status.contains("watching"))
			return Activity.watching(activity);
		else if (status.contains("listening"))
			return Activity.listening(activity);
		else
			return null;
	}
}