package com.github.black0nion.blackonionbot.commands.admin;

import java.time.Duration;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.utils.ValueManager;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class ActivityCommand extends SlashCommand {

    public ActivityCommand() {
	this.setData(new CommandData("activity", "Sets the activity of the bot").addOptions(new OptionData(OptionType.STRING, "type", "The type of the activity", true).addChoice("Playing", "playing").addChoice("Watching", "watching").addChoice("Listening", "listening"), new OptionData(OptionType.STRING, "message", "The custom message to stand after the type"))).setHidden();
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

	final String status = String.join(" ", Utils.subArray(args, 2));

	if (activityType.contains("playing")) {
	    e.getJDA().getPresence().setActivity(Activity.playing(status));
	} else if (activityType.contains("watching")) {
	    e.getJDA().getPresence().setActivity(Activity.watching(status));
	} else if (activityType.contains("listening")) {
	    e.getJDA().getPresence().setActivity(Activity.listening(status));
	} else {
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
	if (status.contains("playing")) return Activity.playing(activity);
	else if (status.contains("watching")) return Activity.watching(activity);
	else if (status.contains("listening")) return Activity.listening(activity);
	else return null;
    }
}