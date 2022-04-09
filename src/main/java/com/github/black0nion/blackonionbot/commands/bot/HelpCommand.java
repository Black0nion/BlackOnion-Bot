package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.SlashCommandBase;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.Progress;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.google.common.collect.Lists;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class HelpCommand extends SlashCommand {
	private static final String COMMANDS = "commands";
	private static final String COMMANDS_NAME  = "name";
	private static final String CATEGORY = "category";
	private static final String CATEGORY_NAME = "name";

	public HelpCommand() {
		super(builder(Commands.slash("help", "used to get help")
				.addSubcommands(
				new SubcommandData(COMMANDS, "used to get help for all commands")
						.addOption(OptionType.STRING,COMMANDS_NAME, "name of the command to get help for", true),
				new SubcommandData(CATEGORY, "used to get help for all commands in a category")
						.addOption(OptionType.STRING, CATEGORY_NAME, "name of the category to get help for", true))
		));
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		switch (e.getSubcommandName()) {
			case COMMANDS -> commands(cmde, e, member, author, guild, channel);
			case CATEGORY -> category(cmde, e, member, author, guild, channel);
			default -> cmde.sendPleaseUse();
		}
	}

	private void commands(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		var commandName = e.getOption(COMMANDS_NAME, OptionMapping::getAsString);
	}

	private void category(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		var categoryName = e.getOption(CATEGORY_NAME, OptionMapping::getAsString);

	}

	private void waitForHelpCatSelection(final BlackMember author, final SlashCommandEvent cmde, final SlashCommandInteraction interaction, TextChannel channel) {
		Bot.getInstance().getEventWaiter().waitForEvent(ButtonInteractionEvent.class, event -> interaction.getTextChannel().getIdLong() == event.getChannel().getIdLong() && interaction.getIdLong() == event.getMessageIdLong() && !event.getUser().isBot() && event.getUser().getIdLong() == author.getIdLong(), event -> {
			final Button button = event.getButton();

			final BlackGuild guild = cmde.getGuild();
			final BlackUser user = cmde.getUser();

			final EmbedBuilder builder = cmde.success().setDescription(LanguageSystem.getTranslation("onlyexecutorcancontrol", user, guild));

			if (Objects.equals(button.getId(), "overview")) {
				builder.setTitle(LanguageSystem.getTranslation("help", user, guild) + " | " + LanguageSystem.getTranslation("modules", user, guild));

				final Category[] cats = Category.values();
				for (int i = 0; i <= cats.length; i++) {
					StringBuilder commandsInCategory = new StringBuilder();
					Category category = null;
					if (i == 0) {
						commandsInCategory = new StringBuilder(", " + cmde.getTranslationOrEmpty("helpmodules"));
					} else {
						category = cats[i - 1];
						for (final SlashCommand c : SlashCommandBase.commandsInCategory.get(category)) {
							if (c.isHidden(user)) {
								commandsInCategory.append(", ").append(c.getName());
							}
						}
					}

					if (commandsInCategory.length() <= 2) {
						continue;
					}
					builder.addField(Utils.firstLetterUppercase((category != null ? category.name() : LanguageSystem.getTranslation("modules", user, guild)).toLowerCase()), commandsInCategory.substring(1), false);
				}
			} else if (Objects.equals(button.getId(), "close")) {
				channel.delete().queue();
				return;
			} else {
				final Category category = Category.valueOf(button.getId());
				builder.setTitle(LanguageSystem.getTranslation("help", user, guild) + " | " + category.name().toUpperCase());
				for (final Map.Entry<String[], SlashCommand> entry : SlashCommandBase.commands.entrySet())
					if (entry.getValue().isHidden(user) && (entry.getValue().getCategory() == category))
						if (entry.getValue().getProgress() == Progress.DONE) {
							builder.addField(SlashCommandEvent.getCommandHelp(entry.getValue()), cmde.getTranslationOrEmpty("help" + entry.getValue().getName()), false);
						}

				for (final Progress pr : Progress.values()) {
					if (pr == Progress.DONE) {
						continue;
					}
					for (final Map.Entry<String[], SlashCommand> entry : SlashCommandBase.commands.entrySet()) {
						final SlashCommand command = entry.getValue();
						if (command.isHidden(user) && (command.getCategory() == category) && command.getProgress() == pr) {
							final String commandHelp = cmde.getTranslation("help" + entry.getValue().getName().toLowerCase());
							if (commandHelp == null) {
								System.out.println("Help for " + entry.getKey() + " not set!");
							}
							builder.addField(pr.name().toUpperCase() + ": " + SlashCommandEvent.getCommandHelp(entry.getValue()), cmde.getTranslationOrEmpty("help" + entry.getValue().getName()), false);
						}
					}
				}
			}

			event.editMessageEmbeds(builder.build()).queue();
			this.waitForHelpCatSelection(author, cmde, interaction, channel);
		}, 5, TimeUnit.MINUTES, () -> channel.editMessage(cmde.getTranslation("helpmenuexpired", new Placeholder("cmd", cmde.getGuild().getPrefix() + this.getName()))).setEmbeds().setActionRows().queue());
	}
}