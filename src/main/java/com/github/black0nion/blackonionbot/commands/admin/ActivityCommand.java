package com.github.black0nion.blackonionbot.commands.admin;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.utils.config.Config;
import com.github.black0nion.blackonionbot.utils.config.ConfigManager;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.time.Duration;

public class ActivityCommand extends Command {

	public ActivityCommand() {
		this.setCommand("activity").setSyntax("([playing | watching | listening] <Text>) | clear").setRequiredArgumentCount(1).setHidden().setCategory(Category.BOT);
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		message.delete().queue();
		final String activityType = args[1].toLowerCase();

		if (activityType.equalsIgnoreCase("clear")) {
			Config.activity_type = null;
			ConfigManager.saveConfig();
			Bot.restartSwitchingStatus(e.getJDA());
			message.replyEmbeds(EmbedUtils.getSuccessEmbed(author, guild).addField("activitycleared", "theactivitygotcleared", false).build()).delay(Duration.ofSeconds(3)).flatMap(Message::delete).queue();
			return;
		}

		final String status = String.join(" ", Utils.subArray(args, 2));

		Activity.ActivityType type = parse(activityType);
		if (type != null) {
			Activity newActivity = getActivity(type, status);
			e.getJDA().getPresence().setActivity(newActivity);
			Config.activity_type = type;
			Config.activity_name = status;
			ConfigManager.saveConfig();
			message.replyEmbeds(EmbedUtils.getSuccessEmbed(author, guild).addField("newactivity", args[1] + " " + status, false).build()).delay(Duration.ofSeconds(3)).flatMap(Message::delete).queue();
		} else {
			message.replyEmbeds(EmbedUtils.getErrorEmbed(author, guild).addField(LanguageSystem.getTranslation("wrongargument", author, guild), CommandEvent.getPleaseUse(guild, author, this), false).build()).delay(Duration.ofSeconds(3)).flatMap(Message::delete).queue();
		}
	}

	private Activity.ActivityType parse(String activityType) {
		try {
			return Activity.ActivityType.valueOf(activityType.toUpperCase());
		} catch (Exception ignored) {
			return null;
		}
	}

	public static Activity getActivity() {
		return getActivity(Config.activity_type, Config.activity_name);
	}

	public static Activity getActivity(final Activity.ActivityType status, final String activity) {
		if (status == null || activity == null) {
			return null;
		}
		return Activity.of(status, activity);
	}
}