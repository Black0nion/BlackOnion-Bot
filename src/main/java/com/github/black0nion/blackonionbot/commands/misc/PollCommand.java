package com.github.black0nion.blackonionbot.commands.misc;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Objects;

public class PollCommand extends SlashCommand {
    private static final String TITLE = "title";
    //option commands
    private static final String TWO_OPTIONS = "two_options";
    private static final String THREE_OPTIONS = "three_options";
    private static final String FOUR_OPTIONS = "four_options";
    private static final String FIVE_OPTIONS = "five_options";
    private static final String SIX_OPTIONS = "six_options";
    private static final String SEVEN_OPTIONS = "seven_options";
    private static final String EIGHT_OPTIONS = "eight_options";
    private static final String NINE_OPTIONS = "nine_options";
    private static final String TEN_OPTIONS = "ten_options";
    //options
    private static final String OPTION_ONE = "option_one";
    private static final String OPTION_TWO = "option_two";
    private static final String OPTION_THREE = "option_three";
    private static final String OPTION_FOUR = "option_four";
    private static final String OPTION_FIVE = "option_five";
    private static final String OPTION_SIX = "option_six";
    private static final String OPTION_SEVEN = "option_seven";
    private static final String OPTION_EIGHT = "option_eight";
    private static final String OPTION_NINE = "option_nine";
    private static final String OPTION_TEN = "option_ten";

    public PollCommand() {
        super(builder(Commands.slash("poll", "Used to create a poll.")
                .addSubcommands(
                        new SubcommandData(TWO_OPTIONS, "Used to create a poll with two options.")
                                .addOption(OptionType.STRING, TITLE, "The title of the poll.", true)
                                .addOption(OptionType.STRING, OPTION_ONE, "The first option.", true)
                                .addOption(OptionType.STRING, OPTION_TWO, "The second option.", true),
                        new SubcommandData(THREE_OPTIONS, "Used to create a poll with three options.")
                                .addOption(OptionType.STRING, TITLE, "The title of the poll.", true)
                                .addOption(OptionType.STRING, OPTION_ONE, "The first option.", true)
                                .addOption(OptionType.STRING, OPTION_TWO, "The second option.", true)
                                .addOption(OptionType.STRING, OPTION_THREE, "The third option.", true),
                        new SubcommandData(FOUR_OPTIONS, "Used to create a poll with four options.")
                                .addOption(OptionType.STRING, TITLE, "The title of the poll.", true)
                                .addOption(OptionType.STRING, OPTION_ONE, "The first option.", true)
                                .addOption(OptionType.STRING, OPTION_TWO, "The second option.", true)
                                .addOption(OptionType.STRING, OPTION_THREE, "The third option.", true)
                                .addOption(OptionType.STRING, OPTION_FOUR, "The fourth option.", true),
                        new SubcommandData(FIVE_OPTIONS, "Used to create a poll with five options.")
                                .addOption(OptionType.STRING, TITLE, "The title of the poll.", true)
                                .addOption(OptionType.STRING, OPTION_ONE, "The first option.", true)
                                .addOption(OptionType.STRING, OPTION_TWO, "The second option.", true)
                                .addOption(OptionType.STRING, OPTION_THREE, "The third option.", true)
                                .addOption(OptionType.STRING, OPTION_FOUR, "The fourth option.", true)
                                .addOption(OptionType.STRING, OPTION_FIVE, "The fifth option.", true),
                        new SubcommandData(SIX_OPTIONS, "Used to create a poll with six options.")
                                .addOption(OptionType.STRING, TITLE, "The title of the poll.", true)
                                .addOption(OptionType.STRING, OPTION_ONE, "The first option.", true)
                                .addOption(OptionType.STRING, OPTION_TWO, "The second option.", true)
                                .addOption(OptionType.STRING, OPTION_THREE, "The third option.", true)
                                .addOption(OptionType.STRING, OPTION_FOUR, "The fourth option.", true)
                                .addOption(OptionType.STRING, OPTION_FIVE, "The fifth option.", true)
                                .addOption(OptionType.STRING, OPTION_SIX, "The sixth option.", true),
                        new SubcommandData(SEVEN_OPTIONS, "Used to create a poll with seven options.")
                                .addOption(OptionType.STRING, TITLE, "The title of the poll.", true)
                                .addOption(OptionType.STRING, OPTION_ONE, "The first option.", true)
                                .addOption(OptionType.STRING, OPTION_TWO, "The second option.", true)
                                .addOption(OptionType.STRING, OPTION_THREE, "The third option.", true)
                                .addOption(OptionType.STRING, OPTION_FOUR, "The fourth option.", true)
                                .addOption(OptionType.STRING, OPTION_FIVE, "The fifth option.", true)
                                .addOption(OptionType.STRING, OPTION_SIX, "The sixth option.", true)
                                .addOption(OptionType.STRING, OPTION_SEVEN, "The seventh option.", true),
                        new SubcommandData(EIGHT_OPTIONS, "Used to create a poll with eight options.")
                                .addOption(OptionType.STRING, TITLE, "The title of the poll.", true)
                                .addOption(OptionType.STRING, OPTION_ONE, "The first option.", true)
                                .addOption(OptionType.STRING, OPTION_TWO, "The second option.", true)
                                .addOption(OptionType.STRING, OPTION_THREE, "The third option.", true)
                                .addOption(OptionType.STRING, OPTION_FOUR, "The fourth option.", true)
                                .addOption(OptionType.STRING, OPTION_FIVE, "The fifth option.", true)
                                .addOption(OptionType.STRING, OPTION_SIX, "The sixth option.", true)
                                .addOption(OptionType.STRING, OPTION_SEVEN, "The seventh option.", true)
                                .addOption(OptionType.STRING, OPTION_EIGHT, "The eighth option.", true),
                        new SubcommandData(NINE_OPTIONS, "Used to create a poll with nine options.")
                                .addOption(OptionType.STRING, TITLE, "The title of the poll.", true)
                                .addOption(OptionType.STRING, OPTION_ONE, "The first option.", true)
                                .addOption(OptionType.STRING, OPTION_TWO, "The second option.", true)
                                .addOption(OptionType.STRING, OPTION_THREE, "The third option.", true)
                                .addOption(OptionType.STRING, OPTION_FOUR, "The fourth option.", true)
                                .addOption(OptionType.STRING, OPTION_FIVE, "The fifth option.", true)
                                .addOption(OptionType.STRING, OPTION_SIX, "The sixth option.", true)
                                .addOption(OptionType.STRING, OPTION_SEVEN, "The seventh option.", true)
                                .addOption(OptionType.STRING, OPTION_EIGHT, "The eighth option.", true)
                                .addOption(OptionType.STRING, OPTION_NINE, "The ninth option.", true),
                        new SubcommandData(TEN_OPTIONS, "Used to create a poll with ten options.")
                                .addOption(OptionType.STRING, TITLE, "The title of the poll.", true)
                                .addOption(OptionType.STRING, OPTION_ONE, "The first option.", true)
                                .addOption(OptionType.STRING, OPTION_TWO, "The second option.", true)
                                .addOption(OptionType.STRING, OPTION_THREE, "The third option.", true)
                                .addOption(OptionType.STRING, OPTION_FOUR, "The fourth option.", true)
                                .addOption(OptionType.STRING, OPTION_FIVE, "The fifth option.", true)
                                .addOption(OptionType.STRING, OPTION_SIX, "The sixth option.", true)
                                .addOption(OptionType.STRING, OPTION_SEVEN, "The seventh option.", true)
                                .addOption(OptionType.STRING, OPTION_EIGHT, "The eighth option.", true)
                                .addOption(OptionType.STRING, OPTION_NINE, "The ninth option.", true)
                                .addOption(OptionType.STRING, OPTION_TEN, "The tenth option.", true)))
                .setRequiredPermissions(Permission.MANAGE_CHANNEL));
    }

    @Override
    public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
        var pollChannel = guild.getPollChannel();
        if (pollChannel != null) {
            switch (Objects.requireNonNull(e.getSubcommandName())) {
                case TWO_OPTIONS -> twoOptions(cmde, e, pollChannel, author);
                case THREE_OPTIONS -> threeOptions(cmde, e, pollChannel, author);
                case FOUR_OPTIONS -> fourOptions(cmde, e, pollChannel, author);
                case FIVE_OPTIONS -> fiveOptions(cmde, e, pollChannel, author);
                case SIX_OPTIONS -> sixOptions(cmde, e, pollChannel, author);
                case SEVEN_OPTIONS -> sevenOptions(cmde, e, pollChannel, author);
                case EIGHT_OPTIONS -> eightOptions(cmde, e, pollChannel, author);
                case NINE_OPTIONS -> nineOptions(cmde, e, pollChannel, author);
                case TEN_OPTIONS -> tenOptions(cmde, e, pollChannel, author);
                default -> cmde.sendPleaseUse();
            }
        }
    }

    public void twoOptions(SlashCommandEvent cmde, SlashCommandInteractionEvent e, TextChannel pollChannel, BlackUser author) {
        var title = e.getOption(TITLE, OptionMapping::getAsString);
        var optionOne = e.getOption(OPTION_ONE, OptionMapping::getAsString);
        var optionTwo = e.getOption(OPTION_TWO, OptionMapping::getAsString);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(title);
        embed.addField("Option 1", optionOne, true);
        embed.addField("Option 2", optionTwo, true);
        embed.setFooter("Poll created by " + author.getName(), author.getAvatarUrl());
        pollChannel.sendMessageEmbeds(embed.build())
                .queue(
                        message -> {
                            message.addReaction("\\u0031\\u20E3").queue();
                            message.addReaction("\\u0032\\u20E3").queue();
                        }
                );
        cmde.send("pollcreated");
    }

    public void threeOptions(SlashCommandEvent cmde, SlashCommandInteractionEvent e, TextChannel pollChannel, BlackUser author) {
        var title = e.getOption(TITLE, OptionMapping::getAsString);
        var optionOne = e.getOption(OPTION_ONE, OptionMapping::getAsString);
        var optionTwo = e.getOption(OPTION_TWO, OptionMapping::getAsString);
        var optionThree = e.getOption(OPTION_THREE, OptionMapping::getAsString);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(title);
        embed.addField("Option 1", optionOne, true);
        embed.addField("Option 2", optionTwo, true);
        embed.addField("Option 3", optionThree, true);
        embed.setFooter("Poll created by " + author.getName(), author.getAvatarUrl());
        pollChannel.sendMessageEmbeds(embed.build())
                .queue(
                        message -> {
                            message.addReaction("\\u0031\\u20E3").queue();
                            message.addReaction("\\u0032\\u20E3").queue();
                            message.addReaction("\\u0033\\u20E3").queue();
                        }
                );
        cmde.send("pollcreated");
    }

    public void fourOptions(SlashCommandEvent cmde, SlashCommandInteractionEvent e, TextChannel pollChannel, BlackUser author) {
        var title = e.getOption(TITLE, OptionMapping::getAsString);
        var optionOne = e.getOption(OPTION_ONE, OptionMapping::getAsString);
        var optionTwo = e.getOption(OPTION_TWO, OptionMapping::getAsString);
        var optionThree = e.getOption(OPTION_THREE, OptionMapping::getAsString);
        var optionFour = e.getOption(OPTION_FOUR, OptionMapping::getAsString);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(title);
        embed.addField("Option 1", optionOne, true);
        embed.addField("Option 2", optionTwo, true);
        embed.addField("Option 3", optionThree, true);
        embed.addField("Option 4", optionFour, true);
        embed.setFooter("Poll created by " + author.getName(), author.getAvatarUrl());
        pollChannel.sendMessageEmbeds(embed.build())
                .queue(
                        message -> {
                            message.addReaction("\\u0031\\u20E3").queue();
                            message.addReaction("\\u0032\\u20E3").queue();
                            message.addReaction("\\u0033\\u20E3").queue();
                            message.addReaction("\\u0034\\u20E3").queue();
                        }
                );
        cmde.send("pollcreated");
    }

    public void fiveOptions(SlashCommandEvent cmde, SlashCommandInteractionEvent e, TextChannel pollChannel, BlackUser author) {
        var title = e.getOption(TITLE, OptionMapping::getAsString);
        var optionOne = e.getOption(OPTION_ONE, OptionMapping::getAsString);
        var optionTwo = e.getOption(OPTION_TWO, OptionMapping::getAsString);
        var optionThree = e.getOption(OPTION_THREE, OptionMapping::getAsString);
        var optionFour = e.getOption(OPTION_FOUR, OptionMapping::getAsString);
        var optionFive = e.getOption(OPTION_FIVE, OptionMapping::getAsString);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(title);
        embed.addField("Option 1", optionOne, true);
        embed.addField("Option 2", optionTwo, true);
        embed.addField("Option 3", optionThree, true);
        embed.addField("Option 4", optionFour, true);
        embed.addField("Option 5", optionFive, true);
        embed.setFooter("Poll created by " + author.getName(), author.getAvatarUrl());
        pollChannel.sendMessageEmbeds(embed.build())
                .queue(
                        message -> {
                            message.addReaction("\\u0031\\u20E3").queue();
                            message.addReaction("\\u0032\\u20E3").queue();
                            message.addReaction("\\u0033\\u20E3").queue();
                            message.addReaction("\\u0034\\u20E3").queue();
                            message.addReaction("\\u0035\\u20E3").queue();
                        }
                );
        cmde.send("pollcreated");
    }

    public void sixOptions(SlashCommandEvent cmde, SlashCommandInteractionEvent e, TextChannel pollChannel, BlackUser author) {
        var title = e.getOption(TITLE, OptionMapping::getAsString);
        var optionOne = e.getOption(OPTION_ONE, OptionMapping::getAsString);
        var optionTwo = e.getOption(OPTION_TWO, OptionMapping::getAsString);
        var optionThree = e.getOption(OPTION_THREE, OptionMapping::getAsString);
        var optionFour = e.getOption(OPTION_FOUR, OptionMapping::getAsString);
        var optionFive = e.getOption(OPTION_FIVE, OptionMapping::getAsString);
        var optionSix = e.getOption(OPTION_SIX, OptionMapping::getAsString);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(title);
        embed.addField("Option 1", optionOne, true);
        embed.addField("Option 2", optionTwo, true);
        embed.addField("Option 3", optionThree, true);
        embed.addField("Option 4", optionFour, true);
        embed.addField("Option 5", optionFive, true);
        embed.addField("Option 6", optionSix, true);
        embed.setFooter("Poll created by " + author.getName(), author.getAvatarUrl());
        pollChannel.sendMessageEmbeds(embed.build())
                .queue(
                        message -> {
                            message.addReaction("\\u0031\\u20E3").queue();
                            message.addReaction("\\u0032\\u20E3").queue();
                            message.addReaction("\\u0033\\u20E3").queue();
                            message.addReaction("\\u0034\\u20E3").queue();
                            message.addReaction("\\u0035\\u20E3").queue();
                            message.addReaction("\\u0036\\u20E3").queue();
                        }
                );
        cmde.send("pollcreated");
    }

    public void sevenOptions(SlashCommandEvent cmde, SlashCommandInteractionEvent e, TextChannel pollChannel, BlackUser author) {
        var title = e.getOption(TITLE, OptionMapping::getAsString);
        var optionOne = e.getOption(OPTION_ONE, OptionMapping::getAsString);
        var optionTwo = e.getOption(OPTION_TWO, OptionMapping::getAsString);
        var optionThree = e.getOption(OPTION_THREE, OptionMapping::getAsString);
        var optionFour = e.getOption(OPTION_FOUR, OptionMapping::getAsString);
        var optionFive = e.getOption(OPTION_FIVE, OptionMapping::getAsString);
        var optionSix = e.getOption(OPTION_SIX, OptionMapping::getAsString);
        var optionSeven = e.getOption(OPTION_SEVEN, OptionMapping::getAsString);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(title);
        embed.addField("Option 1", optionOne, true);
        embed.addField("Option 2", optionTwo, true);
        embed.addField("Option 3", optionThree, true);
        embed.addField("Option 4", optionFour, true);
        embed.addField("Option 5", optionFive, true);
        embed.addField("Option 6", optionSix, true);
        embed.addField("Option 7", optionSeven, true);
        embed.setFooter("Poll created by " + author.getName(), author.getAvatarUrl());
        pollChannel.sendMessageEmbeds(embed.build())
                .queue(
                        message -> {
                            message.addReaction("\\u0031\\u20E3").queue();
                            message.addReaction("\\u0032\\u20E3").queue();
                            message.addReaction("\\u0033\\u20E3").queue();
                            message.addReaction("\\u0034\\u20E3").queue();
                            message.addReaction("\\u0035\\u20E3").queue();
                            message.addReaction("\\u0036\\u20E3").queue();
                            message.addReaction("\\u0037\\u20E3").queue();
                        }
                );
        cmde.send("pollcreated");
    }

    public void eightOptions(SlashCommandEvent cmde, SlashCommandInteractionEvent e, TextChannel pollChannel, BlackUser author) {
        var title = e.getOption(TITLE, OptionMapping::getAsString);
        var optionOne = e.getOption(OPTION_ONE, OptionMapping::getAsString);
        var optionTwo = e.getOption(OPTION_TWO, OptionMapping::getAsString);
        var optionThree = e.getOption(OPTION_THREE, OptionMapping::getAsString);
        var optionFour = e.getOption(OPTION_FOUR, OptionMapping::getAsString);
        var optionFive = e.getOption(OPTION_FIVE, OptionMapping::getAsString);
        var optionSix = e.getOption(OPTION_SIX, OptionMapping::getAsString);
        var optionSeven = e.getOption(OPTION_SEVEN, OptionMapping::getAsString);
        var optionEight = e.getOption(OPTION_EIGHT, OptionMapping::getAsString);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(title);
        embed.addField("Option 1", optionOne, true);
        embed.addField("Option 2", optionTwo, true);
        embed.addField("Option 3", optionThree, true);
        embed.addField("Option 4", optionFour, true);
        embed.addField("Option 5", optionFive, true);
        embed.addField("Option 6", optionSix, true);
        embed.addField("Option 7", optionSeven, true);
        embed.addField("Option 8", optionEight, true);
        embed.setFooter("Poll created by " + author.getName(), author.getAvatarUrl());
        pollChannel.sendMessageEmbeds(embed.build())
                .queue(
                        message -> {
                            message.addReaction("\\u0031\\u20E3").queue();
                            message.addReaction("\\u0032\\u20E3").queue();
                            message.addReaction("\\u0033\\u20E3").queue();
                            message.addReaction("\\u0034\\u20E3").queue();
                            message.addReaction("\\u0035\\u20E3").queue();
                            message.addReaction("\\u0036\\u20E3").queue();
                            message.addReaction("\\u0037\\u20E3").queue();
                            message.addReaction("\\u0038\\u20E3").queue();
                        }
                );
        cmde.send("pollcreated");
    }

    public void nineOptions(SlashCommandEvent cmde, SlashCommandInteractionEvent e, TextChannel pollChannel, BlackUser author) {
        var title = e.getOption(TITLE, OptionMapping::getAsString);
        var optionOne = e.getOption(OPTION_ONE, OptionMapping::getAsString);
        var optionTwo = e.getOption(OPTION_TWO, OptionMapping::getAsString);
        var optionThree = e.getOption(OPTION_THREE, OptionMapping::getAsString);
        var optionFour = e.getOption(OPTION_FOUR, OptionMapping::getAsString);
        var optionFive = e.getOption(OPTION_FIVE, OptionMapping::getAsString);
        var optionSix = e.getOption(OPTION_SIX, OptionMapping::getAsString);
        var optionSeven = e.getOption(OPTION_SEVEN, OptionMapping::getAsString);
        var optionEight = e.getOption(OPTION_EIGHT, OptionMapping::getAsString);
        var optionNine = e.getOption(OPTION_NINE, OptionMapping::getAsString);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(title);
        embed.addField("Option 1", optionOne, true);
        embed.addField("Option 2", optionTwo, true);
        embed.addField("Option 3", optionThree, true);
        embed.addField("Option 4", optionFour, true);
        embed.addField("Option 5", optionFive, true);
        embed.addField("Option 6", optionSix, true);
        embed.addField("Option 7", optionSeven, true);
        embed.addField("Option 8", optionEight, true);
        embed.addField("Option 9", optionNine, true);
        embed.setFooter("Poll created by " + author.getName(), author.getAvatarUrl());
        pollChannel.sendMessageEmbeds(embed.build())
                .queue(
                        message -> {
                            message.addReaction("\\u0031\\u20E3").queue();
                            message.addReaction("\\u0032\\u20E3").queue();
                            message.addReaction("\\u0033\\u20E3").queue();
                            message.addReaction("\\u0034\\u20E3").queue();
                            message.addReaction("\\u0035\\u20E3").queue();
                            message.addReaction("\\u0036\\u20E3").queue();
                            message.addReaction("\\u0037\\u20E3").queue();
                            message.addReaction("\\u0038\\u20E3").queue();
                            message.addReaction("\\u0039\\u20E3").queue();
                        }
                );
        cmde.send("pollcreated");
    }

    public void tenOptions(SlashCommandEvent cmde, SlashCommandInteractionEvent e, TextChannel pollChannel, BlackUser author) {
        var title = e.getOption(TITLE, OptionMapping::getAsString);
        var optionOne = e.getOption(OPTION_ONE, OptionMapping::getAsString);
        var optionTwo = e.getOption(OPTION_TWO, OptionMapping::getAsString);
        var optionThree = e.getOption(OPTION_THREE, OptionMapping::getAsString);
        var optionFour = e.getOption(OPTION_FOUR, OptionMapping::getAsString);
        var optionFive = e.getOption(OPTION_FIVE, OptionMapping::getAsString);
        var optionSix = e.getOption(OPTION_SIX, OptionMapping::getAsString);
        var optionSeven = e.getOption(OPTION_SEVEN, OptionMapping::getAsString);
        var optionEight = e.getOption(OPTION_EIGHT, OptionMapping::getAsString);
        var optionNine = e.getOption(OPTION_NINE, OptionMapping::getAsString);
        var optionTen = e.getOption(OPTION_TEN, OptionMapping::getAsString);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(title);
        embed.addField("Option 1", optionOne, true);
        embed.addField("Option 2", optionTwo, true);
        embed.addField("Option 3", optionThree, true);
        embed.addField("Option 4", optionFour, true);
        embed.addField("Option 5", optionFive, true);
        embed.addField("Option 6", optionSix, true);
        embed.addField("Option 7", optionSeven, true);
        embed.addField("Option 8", optionEight, true);
        embed.addField("Option 9", optionNine, true);
        embed.addField("Option 10", optionTen, true);
        embed.setFooter("Poll created by " + author.getName(), author.getAvatarUrl());
        pollChannel.sendMessageEmbeds(embed.build())
                .queue(
                        message -> {
                            message.addReaction("\\u0031\\u20E3").queue();
                            message.addReaction("\\u0032\\u20E3").queue();
                            message.addReaction("\\u0033\\u20E3").queue();
                            message.addReaction("\\u0034\\u20E3").queue();
                            message.addReaction("\\u0035\\u20E3").queue();
                            message.addReaction("\\u0036\\u20E3").queue();
                            message.addReaction("\\u0037\\u20E3").queue();
                            message.addReaction("\\u0038\\u20E3").queue();
                            message.addReaction("\\u0039\\u20E3").queue();
                            message.addReaction("\\uD83D\\uDD1F").queue();
                        }
                );
        cmde.send("pollcreated");
    }
}

