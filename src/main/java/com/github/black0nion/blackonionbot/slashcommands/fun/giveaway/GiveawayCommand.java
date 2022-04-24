package com.github.black0nion.blackonionbot.slashcommands.fun.giveaway;

import com.github.black0nion.blackonionbot.slashcommands.SlashCommand;
import com.github.black0nion.blackonionbot.slashcommands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.systems.giveaways.GiveawaySystem;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

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
                        "The number of winners to give away", true)
                        .setRequiredRange(1, 100))));
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
        if (min == null && hours == null && days == null && weeks == null && months == null && years == null) {
            cmde.send("timenotspecfied");
        } else {
            long timeOut = 0;
            if (min != null) {
                timeOut += min * 60 * 1000;
            } else if (hours != null) {
                timeOut += hours * 60 * 60 * 1000;
            } else if (days != null) {
                timeOut += days * 24 * 60 * 60 * 1000;
            } else if (weeks != null) {
                timeOut += (long) weeks * 7 * 24 * 60 * 60 * 1000;
            } else if (months != null) {
                timeOut += (long) months * 30 * 24 * 60 * 60 * 1000;
            } else if (years != null) {
                timeOut += (long) years * 365 * 24 * 60 * 60 * 1000;
            } else {
                cmde.send("timenotspecfied");
            }
            var data = Date.from(Instant.now().plusMillis(timeOut));
            var item = e.getOption(ITEM_TO_GIVE, OptionMapping::getAsString);
            var winners = e.getOption(WINNERS, OptionMapping::getAsInt);
            final EmbedBuilder giveawayMessage = cmde.success().setTitle(cmde.getTranslation("giveawayfor", new Placeholder("item", item))).setDescription(cmde.getTranslation("giveawaydesc", new Placeholder("item", item), new Placeholder("winners", String.valueOf(winners)), new Placeholder("end", format.format(data).replace("_", " ")), new Placeholder("user", author.getAsMention())));
            cmde.reply(giveawayMessage, msg -> {
                msg.retrieveOriginal().queue(
                        message -> {
                            message.addReaction("U+1F389").queue();
                            message.editMessageEmbeds(giveawayMessage.setFooter(cmde.getTranslation("giveawayid", new Placeholder("id", message.getId()))).build()).queue();
                            GiveawaySystem.createGiveaway(data, message.getIdLong(), channel.getIdLong(), author.getIdLong(), guild.getIdLong(), item, winners);
                        }
                );
            });
        }
    }
}