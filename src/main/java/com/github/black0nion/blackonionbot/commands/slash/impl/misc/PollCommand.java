package com.github.black0nion.blackonionbot.commands.slash.impl.misc;

import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.TranslatedEmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.stream.IntStream;

public class PollCommand extends SlashCommand {

	// used in unit tests, don't change the visibility
	static final List<String> DIGITS_LIST = Arrays.asList("zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten");

	static final List<String> DIGITS_UNICODE = Arrays.asList(
		"U+0030U+20E3", // 0
		"U+0031U+20E3", // 1
		"U+0032U+20E3", // 2
		"U+0033U+20E3", // 3
		"U+0034U+20E3", // 4
		"U+0035U+20E3", // 5
		"U+0036U+20E3", // 6
		"U+0037U+20E3", // 7
		"U+0038U+20E3", // 8
		"U+0039U+20E3", // 9
		"U+1F51F" // 10
	);

	private static final String TOPIC = "topic";
	private static final String OPTION_PREFIX = "option_";

	private static final OptionData[] CHOICES = IntStream.range(0, DIGITS_UNICODE.size())
		.mapToObj(i -> new OptionData(OptionType.STRING, OPTION_PREFIX + DIGITS_LIST.get(i), "Choice #" + i, i <= 2))
		.toArray(OptionData[]::new);

	public PollCommand() {
		super(builder(Commands.slash("poll", "Used to create a poll.")
			.addOption(OptionType.STRING, TOPIC, "Used to set the topic of the poll.", true)
			.addOptions(CHOICES)));
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, Member member, User author, Guild guild, TextChannel pollChannel, UserSettings userSettings, GuildSettings guildSettings) throws Exception {
		TranslatedEmbedBuilder embed = cmde.success();
		embed.setTitle(e.getOption(TOPIC, OptionMapping::getAsString));
		// we don't care about the author's language so we get it from guildSettings
		embed.setFooter(guildSettings.getLanguage().getOrDefault().getTranslation("pollcreatedby", new Placeholder("user", Utils.escapeMarkdown(author.getAsTag()))), author.getEffectiveAvatarUrl());

		boolean hadNullOption = false;
		int index = 0;
		for (OptionData optionData : CHOICES) {
			var option = e.getOption(optionData.getName());
			if (option != null) {
				if (hadNullOption) throw new InputMismatchException("Options have to be filled in order");
				embed.addField(cmde.getTranslation("optionnumber", new Placeholder("num", index)), option.getAsString());
				index++;
			} else {
				hadNullOption = true;
			}
		}
		final int finalIndex = index;
		e.replyEmbeds(embed.build()).flatMap(InteractionHook::retrieveOriginal).queue(msg -> {
			for (int i = 0; i < finalIndex; i++) {
				msg.addReaction(Emoji.fromUnicode(DIGITS_UNICODE.get(i))).queue();
			}
		});
	}
}