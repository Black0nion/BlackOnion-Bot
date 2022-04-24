package com.github.black0nion.blackonionbot.slashcommands.admin;

import com.github.black0nion.blackonionbot.slashcommands.SlashCommand;
import com.github.black0nion.blackonionbot.slashcommands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.config.Config;
import com.github.black0nion.blackonionbot.utils.config.ConfigManager;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
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

    public ActivityCommand() {
        super(builder(Commands.slash("activity", "Set the activity of the bot")
                .addSubcommands(
                        new SubcommandData("set", "Set the activity of the bot").addOptions(
                                new OptionData(OptionType.STRING, "type", "The type of activity", true)
                                        .addChoices(
                                                Arrays.stream(Activity.ActivityType.values())
                                                        .map(a -> new Command.Choice(a.name(), a.name().toLowerCase()))
                                                        .toList()
                                        ),
                                new OptionData(OptionType.STRING, "text", "The text of the activity", true)
                        ), new SubcommandData("clear", "Clear the activity of the bot")))
                .setAdminGuild());
    }

    @Override
    public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
        assert e.getSubcommandName() != null;
        if (e.getSubcommandName().equalsIgnoreCase("set")) {
            if (e.getOption("type") == null || e.getOption("text") == null) {
                cmde.sendPleaseUse();
            }
            final String text = e.getOption("text", OptionMapping::getAsString);
            Activity.ActivityType type = parse(e.getOption("type", OptionMapping::getAsString));
            if (type != null) {
                Activity newActivity = getActivity(type, text);
                e.getJDA().getPresence().setActivity(newActivity);
                Config.activity_type = type;
                Config.activity_name = text;
                ConfigManager.saveConfig();
                cmde.send("newactivity", new Placeholder("newactivity", newActivity.getName()));
            } else cmde.send("invalidactivitytype");
        } else if (e.getSubcommandName().equalsIgnoreCase("clear")) {
            Config.activity_type = null;
            Config.activity_name = null;
            e.getJDA().getPresence().setActivity(null);
            ConfigManager.saveConfig();
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
    public static Activity getActivity() {
        return getActivity(Config.activity_type, Config.activity_name);
    }

    @Nullable
    public static Activity getActivity(final Activity.ActivityType status, final String activity) {
        if (status == null || activity == null) {
            return null;
        }
        return Activity.of(status, activity);
    }
}