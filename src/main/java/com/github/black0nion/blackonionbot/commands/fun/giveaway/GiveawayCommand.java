package com.github.black0nion.blackonionbot.commands.fun.giveaway;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.systems.giveaways.GiveawaySystem;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class GiveawayCommand extends SlashCommand {
    private static final String WINNERS = "winners";
    private static final String ITEM_TO_GIVE = "item";
    // Units
    private static final String MINUTES = "minutes";
    private static final String HOURS = "hours";
    private static final String DAYS = "days";
    private static final String WEEKS = "weeks";
    private static final String MONTHS = "months";
    private static final String YEARS = "years";
    private static final int MAX_TIMEOUT = Integer.MAX_VALUE;

    public GiveawayCommand() {
        super(builder(Commands.slash("giveaway", "Start a giveaway!")
                .addOptions(new OptionData(OptionType.INTEGER, MINUTES,
                        "The length of the time out in minutes", false).setMinValue(1))
                .addOptions(
                        new OptionData(OptionType.INTEGER, HOURS, "The length of the time out in hours",
                                false).setRequiredRange(1, MAX_TIMEOUT))
                .addOptions(
                        new OptionData(OptionType.INTEGER, DAYS, "The length of the time out in days",
                                false).setRequiredRange(1, MAX_TIMEOUT))
                .addOptions(
                        new OptionData(OptionType.INTEGER, WEEKS, "The length of the time out in weeks",
                                false).setRequiredRange(1, MAX_TIMEOUT))
                .addOptions(
                        new OptionData(OptionType.INTEGER, MONTHS, "The length of the time out in months",
                                        false).setRequiredRange(1, MAX_TIMEOUT))
                .addOptions(
                        new OptionData(OptionType.INTEGER, YEARS, "The length of the time out in years",
                                        false).setRequiredRange(1, MAX_TIMEOUT))
                .addOptions(new OptionData(OptionType.STRING, ITEM_TO_GIVE,
                                "The item to give away", true))
                .addOptions(new OptionData(OptionType.INTEGER, WINNERS,
                                        "The number of winners to give away", true))));
    }

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm");

    @Override
    public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
        Integer min = e.getOption(MINUTES, OptionMapping::getAsInt);
        Integer hours = e.getOption(HOURS, OptionMapping::getAsInt);
        Integer days = e.getOption(DAYS, OptionMapping::getAsInt);
        Integer weeks = e.getOption(WEEKS, OptionMapping::getAsInt);
        Integer months = e.getOption(MONTHS, OptionMapping::getAsInt);
        Integer years = e.getOption(YEARS, OptionMapping::getAsInt);


    }
}