package com.github.black0nion.blackonionbot.commands.misc;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.Instant;
import java.util.List;

public class PollCommand extends SlashCommand {
    private static final String TOPIC = "topic";
    private static final String OPTION_ONE_NAME = "option_one";
    private static final String OPTION_TWO_NAME = "option_two";
    private static final String OPTION_THREE_NAME = "option_three";
    private static final String OPTION_FOUR_NAME = "option_four";
    private static final String OPTION_FIVE_NAME = "option_five";
    private static final String OPTION_SIX_NAME = "option_six";
    private static final String OPTION_SEVEN_NAME = "option_seven";
    private static final String OPTION_EIGHT_NAME = "option_eight";
    private static final String OPTION_NINE_NAME = "option_nine";
    private static final String OPTION_TEN_NAME = "option_ten";

    private static final OptionData[] choices = {
            new OptionData(OptionType.STRING, OPTION_ONE_NAME, "Used to add one option to the poll.", true),
            new OptionData(OptionType.STRING, OPTION_TWO_NAME, "Used to add two options to the poll.", true),
            new OptionData(OptionType.STRING, OPTION_THREE_NAME, "Used to add three options to the poll.", false),
            new OptionData(OptionType.STRING, OPTION_FOUR_NAME, "Used to add four options to the poll.", false),
            new OptionData(OptionType.STRING, OPTION_FIVE_NAME, "Used to add five options to the poll.", false),
            new OptionData(OptionType.STRING, OPTION_SIX_NAME, "Used to add six options to the poll.", false),
            new OptionData(OptionType.STRING, OPTION_SEVEN_NAME, "Used to add seven options to the poll.", false),
            new OptionData(OptionType.STRING, OPTION_EIGHT_NAME, "Used to add eight options to the poll.", false),
            new OptionData(OptionType.STRING, OPTION_NINE_NAME, "Used to add nine options to the poll.", false),
            new OptionData(OptionType.STRING, OPTION_TEN_NAME, "Used to add ten options to the poll.", false)
    };

    public PollCommand() {
        super(builder(Commands.slash("poll", "Used to create a poll.")
                .addOption(OptionType.STRING, TOPIC, "Used to set the topic of the poll.", true)
                .addOptions(choices)));
    }

    @Override
    public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
        var optionOne = e.getOption(OPTION_ONE_NAME, OptionMapping::getAsString);
        var optionTwo = e.getOption(OPTION_TWO_NAME, OptionMapping::getAsString);
        var optionThree = e.getOption(OPTION_THREE_NAME, OptionMapping::getAsString);
        var optionFour = e.getOption(OPTION_FOUR_NAME, OptionMapping::getAsString);
        var optionFive = e.getOption(OPTION_FIVE_NAME, OptionMapping::getAsString);
        var optionSix = e.getOption(OPTION_SIX_NAME, OptionMapping::getAsString);
        var optionSeven = e.getOption(OPTION_SEVEN_NAME, OptionMapping::getAsString);
        var optionEight = e.getOption(OPTION_EIGHT_NAME, OptionMapping::getAsString);
        var optionNine = e.getOption(OPTION_NINE_NAME, OptionMapping::getAsString);
        var optionTen = e.getOption(OPTION_TEN_NAME, OptionMapping::getAsString);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(e.getOption(TOPIC, OptionMapping::getAsString));
        embed.addField("Option 1", optionOne, true);
        embed.addField("Option 2", optionTwo, true);
        //Have it were it checks if each option is null, if it is, it will not add it to the embed.
        if (optionThree != null) {
            embed.addField("Option 3", optionThree, true);
        }
        if (optionFour != null) {
            embed.addField("Option 4", optionFour, true);
        }
        if (optionFive != null) {
            embed.addField("Option 5", optionFive, true);
        }
        if (optionSix != null) {
            embed.addField("Option 6", optionSix, true);
        }
        if (optionSeven != null) {
            embed.addField("Option 7", optionSeven, true);
        }
        if (optionEight != null) {
            embed.addField("Option 8", optionEight, true);
        }
        if (optionNine != null) {
            embed.addField("Option 9", optionNine, true);
        }
        if (optionTen != null) {
            embed.addField("Option 10", optionTen, true);
        }
        embed.setFooter("Poll created by " + author.getEscapedEffectiveName() + "#" + author.getDiscriminator(), author.getAvatarUrl());
        embed.setTimestamp(Instant.now());

        
    }

}