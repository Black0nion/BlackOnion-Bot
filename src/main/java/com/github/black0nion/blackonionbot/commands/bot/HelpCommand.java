package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.commands.TextCommand;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.Progress;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class HelpCommand extends SlashCommand {

	public HelpCommand() {
		super(builder(Commands.slash("help","Used to get help on commands.")).notToggleable());
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {

	}

	private void waitForHelpCatSelection(final @NotNull Message msg, final @NotNull BlackMember author, final @NotNull CommandEvent cmde) {
		Bot.getInstance().getEventWaiter().waitForEvent(ButtonInteractionEvent.class, event -> msg.getTextChannel().getIdLong() == event.getChannel().getIdLong() && msg.getIdLong() == event.getMessageIdLong() && !event.getUser().isBot() && event.getUser().getIdLong() == author.getIdLong(), event -> {
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
						for (final TextCommand c : CommandBase.commandsInCategory.get(category)) {
							if (c.isVisible(user)) {
								commandsInCategory.append(", ").append(c.getCommand()[0]);
							}
						}
					}

					if (commandsInCategory.length() <= 2) {
						continue;
					}
					builder.addField(Utils.firstLetterUppercase((category != null ? category.name() : LanguageSystem.getTranslation("modules", user, guild)).toLowerCase()), commandsInCategory.substring(1), false);
				}
			} else if (Objects.equals(button.getId(), "close")) {
				msg.delete().queue();
				return;
			} else {
				final Category category = Category.valueOf(button.getId());
				builder.setTitle(LanguageSystem.getTranslation("help", user, guild) + " | " + category.name().toUpperCase());
				for (final Map.Entry<String[], TextCommand> entry : CommandBase.commandsArray.entrySet())
					if (entry.getValue().isVisible(user) && (entry.getValue().getCategory() == category))
						if (entry.getValue().getProgress() == Progress.DONE) {
							builder.addField(CommandEvent.getCommandHelp(guild, entry.getValue()), cmde.getTranslationOrEmpty("help" + entry.getValue().getCommand()[0]), false);
						}

				for (final Progress pr : Progress.values()) {
					if (pr == Progress.DONE) {
						continue;
					}
					for (final Map.Entry<String[], TextCommand> entry : CommandBase.commandsArray.entrySet()) {
						final TextCommand command = entry.getValue();
						if (command.isVisible(user) && (command.getCategory() == category) && command.getProgress() == pr) {
							final String commandHelp = cmde.getTranslation("help" + entry.getValue().getCommand()[0].toLowerCase());
							if (commandHelp == null) {
								System.out.println("Help for " + entry.getKey()[0] + " not set!");
							}
							builder.addField(pr.name().toUpperCase() + ": " + CommandEvent.getCommandHelp(guild, entry.getValue()), cmde.getTranslationOrEmpty("help" + entry.getValue().getCommand()[0]), false);
						}
					}
				}
			}

			event.editMessageEmbeds(builder.build()).queue();
			this.waitForHelpCatSelection(msg, author, cmde);
		}, 5, TimeUnit.MINUTES, () -> msg.editMessage(cmde.getTranslation("helpmenuexpired", new Placeholder("cmd", cmde.getGuild().getPrefix() + this.getCommand()[0]))).setEmbeds().setActionRows().queue());
	}
}