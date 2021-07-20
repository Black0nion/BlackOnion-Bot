package com.github.black0nion.blackonionbot.commands.admin;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.misc.CustomPermission;
import com.github.black0nion.blackonionbot.utils.ValueManager;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class ActivityCommand extends SlashCommand {

    public ActivityCommand() {
	this.setData(new CommandData("activity", "Sets the activity of the bot").addOptions(new OptionData(OptionType.STRING, "type", "The type of the activity", true).addChoice("Playing", "playing").addChoice("Watching", "watching").addChoice("Listening", "listening").addChoice("Streaming", "streaming").addChoice("Competing", "competing"), new OptionData(OptionType.STRING, "message", "The custom message to stand after the type", true), new OptionData(OptionType.STRING, "url", "The URL of the stream (required for the STREAMING status)"))).permissions(CustomPermission.SET_ACTIVITY);
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final String activityType = e.getOptionsByName("type").get(0).getAsString();

	if (activityType.equalsIgnoreCase("clear")) {
	    ValueManager.remove("activityType");
	    Bot.restartSwitchingStatus(e.getJDA());
	    cmde.successPrivate("activitycleared", "theactivitygotcleared");
	    return;
	}

	if (e.getOptionsByName("message").size() == 0) {
	    cmde.sendPleaseUsePrivate();
	    return;
	}

	final String status = e.getOptionsByName("message").get(0).getAsString();

	if (activityType.contains("playing")) {
	    e.getJDA().getPresence().setActivity(Activity.playing(status));
	} else if (activityType.contains("watching")) {
	    e.getJDA().getPresence().setActivity(Activity.watching(status));
	} else if (activityType.contains("listening")) {
	    e.getJDA().getPresence().setActivity(Activity.listening(status));
	} else if (activityType.contains("streaming")) {
	    final OptionMapping option = e.getOption("url");
	    if (option == null) {
		cmde.errorPrivate("notwitchurl", "pleasevalidtwitchurl");
		return;
	    }
	    final String url = option.getAsString();
	    if (url.isEmpty() || !Activity.isValidStreamingUrl(url)) {
		cmde.errorPrivate("notwitchurl", "pleasevalidtwitchurl");
		return;
	    }
	    ValueManager.save("activityUrl", url);
	    e.getJDA().getPresence().setActivity(Activity.streaming(status, url));
	} else if (activityType.contains("competing")) {
	    e.getJDA().getPresence().setActivity(Activity.competing(status));
	} else {
	    cmde.errorPrivate("wrongargument", SlashCommandExecutedEvent.getPleaseUse(guild, author, this));
	    return;
	}
	cmde.successPrivate("newactivity", "**" + activityType + "** " + status);
	ValueManager.save("activityType", activityType);
	ValueManager.save("activity", status);
    }

    public static Activity getActivity() {
	return getActivity(ValueManager.getString("activityType"), ValueManager.getString("activity"), ValueManager.getString("activityUrl"));
    }

    public static Activity getActivity(final String status, final String activity, final String url) {
	if (status.contains("playing")) return Activity.playing(activity);
	else if (status.contains("watching")) return Activity.watching(activity);
	else if (status.contains("listening")) return Activity.listening(activity);
	else if (status.contains("streaming")) return Activity.streaming(activity, url);
	else if (status.contains("competing")) return Activity.competing(url);
	else return null;
    }
}