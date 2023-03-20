package com.github.black0nion.blackonionbot.commands.slash.impl.admin;

import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.config.mutable.api.Settings;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import javax.annotation.Nullable;
import java.util.Arrays;

public class ActivityCommand extends SlashCommand {

	private final Settings settings;

	public ActivityCommand(Settings settings) {
		super(builder(Commands.slash("activity", "Set the activity of the bot")
			.addSubcommands(
				new SubcommandData("set", "Set the activity of the bot").addOptions(
					new OptionData(OptionType.STRING, "type", "The type of activity", true)
						.addChoices(
							Arrays.stream(Activity.ActivityType.values())
								.map(a -> new Command.Choice(a.name(), a.name().toLowerCase()))
								.toList()
						),
					new OptionData(OptionType.STRING, "text", "The text of the activity", true),
					new OptionData(OptionType.STRING, "url", "The url of the activity (only for streaming)", false)
				), new SubcommandData("clear", "Clear the activity of the bot")))
			.setAdminGuild());
		this.settings = settings;
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, Member member, User author, BlackGuild guild, TextChannel channel, UserSettings userSettings, GuildSettings guildSettings) throws Exception {
		if (cmde.getSubcommandName().equalsIgnoreCase("set")) {
			final String text = e.getOption("text", OptionMapping::getAsString);
			Activity.ActivityType type = parse(e.getOption("type", OptionMapping::getAsString));
			@Nullable String url = e.getOption("url", OptionMapping::getAsString);
			if (type != null) {
				Activity newActivity = getActivity(type, text, url);
				e.getJDA().getPresence().setActivity(newActivity);

				settings.setActivityType(type);
				settings.setActivityName(text);
				settings.setActivityUrl(url);
				cmde.send("newactivity", new Placeholder("activity",
					newActivity != null ? type + " " + Utils.escapeMarkdown(newActivity.getName()) : cmde.getTranslation("empty")));
			} else cmde.send("invalidactivitytype");
		} else if (cmde.getSubcommandName().equalsIgnoreCase("clear")) {
			settings.setActivityType(null);
			settings.setActivityName(null);
			settings.setActivityUrl(null);
			e.getJDA().getPresence().setActivity(null);
			cmde.send("activitycleared");
		}
	}

	@Nullable
	private Activity.ActivityType parse(String activityType) {
		try {
			return Activity.ActivityType.valueOf(activityType.toUpperCase());
		} catch (Exception ignored) {
			return null;
		}
	}

	@Nullable
	public static Activity getActivity(Settings settings) {
		return getActivity(settings.getActivityType(), settings.getActivityName(), settings.getActivityUrl());
	}

	@Nullable
	public static Activity getActivity(final Activity.ActivityType status, final String activity, String url) {
		if (status == null || activity == null) {
			return null;
		}
		if (status == Activity.ActivityType.STREAMING) {
			return Activity.streaming(activity, url);
		}
		return Activity.of(status, activity);
	}
}
