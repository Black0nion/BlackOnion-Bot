package com.github.black0nion.blackonionbot.commands.slash.impl.information;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.SlashCommandBase;
import com.github.black0nion.blackonionbot.commands.Progress;
import com.github.black0nion.blackonionbot.commands.common.Category;
import com.github.black0nion.blackonionbot.commands.common.Command;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.config.immutable.api.Config;
import com.github.black0nion.blackonionbot.utils.Pair;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.StartsWithLinkedList;
import com.google.common.collect.Lists;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

// TODO: recode without event waiter
public class HelpCommand extends SlashCommand {

	private static final String COMMAND_OR_CATEGORY = "command_or_category";

	private final SlashCommandBase slashCommandBase;

	public HelpCommand(Config config, SlashCommandBase slashCommandBase) {
		super(builder(Commands.slash("help", "Used to get help on commands.")
			.addOption(OptionType.STRING, COMMAND_OR_CATEGORY, "Used to retrieve help for a command / category", false, true))
			.notToggleable(), config);
		this.slashCommandBase = slashCommandBase;
	}

	public void updateAutoComplete() {
		List<String> result = slashCommandBase.getCommands().entrySet().stream()
			.filter(e -> {
				Command currentCommand = e.getValue().getSecond();
				return currentCommand.getRequiredCustomPermissions().isEmpty() && currentCommand.isToggleable();
			})
			.map(Map.Entry::getKey)
			.collect(Collectors.toCollection(StartsWithLinkedList::new));

		Arrays.stream(Category.values()).map(Category::name).forEach(result::add);

		this.updateAutoComplete(COMMAND_OR_CATEGORY, result);
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, @NotNull Member member, @NotNull User author, Guild guild, @NotNull TextChannel channel, UserSettings userSettings, GuildSettings guildSettings) throws Exception {
		var command = e.getOption(COMMAND_OR_CATEGORY, OptionMapping::getAsString);
		if (command != null) {
			for (final Pair<Long, Command> entry : slashCommandBase.getCommands().values()) {
				final Command cmd = entry.getSecond();
				if (!(cmd instanceof SlashCommand slashCommand)) continue;

				if (!slashCommand.isHidden(userSettings) && cmd.getName().equalsIgnoreCase(command)) {
					cmde.success("help", cmde.getCommandHelp(slashCommand), cmde.getTranslationOrEmpty("help" + cmd.getName().toLowerCase()));
					return;
				}
			}

			final Category category = Category.parse(command);
			if (category != null) {
				final EmbedBuilder builder = cmde.success().setTitle(cmde.getTranslation("help") + " | " + category.name());
				for (final Command c : slashCommandBase.getCommandsInCategory().get(category)) {
					if (c instanceof SlashCommand slashCommand)
						builder.addField(cmde.getCommandHelp(slashCommand), cmde.getTranslationOrEmpty("help" + c.getName()), false);
				}
				cmde.reply(builder);
			} else {
				cmde.error("commandnotfound", "thecommandnotfound", new Placeholder("command", "`" + command + "`"));
			}
		} else {
			final EmbedBuilder builder = cmde.success().setTitle(cmde.getTranslation("help") + " | " + cmde.getTranslation("modules")).setDescription(cmde.getTranslation("onlyexecutorcancontrol"));
			final Category[] cats = Category.values();
			final List<Button> buttons = new LinkedList<>();
			for (int i = 0; i <= cats.length; i++) {
				StringBuilder commandsInCategory = new StringBuilder();
				Category category = null;
				if (i == 0) {
					commandsInCategory = new StringBuilder(", " + cmde.getTranslation("helpmodules"));
				} else {
					category = cats[i - 1];
					if (slashCommandBase.getCommandsInCategory().containsKey(category)) {
						for (final Command c : slashCommandBase.getCommandsInCategory().get(category)) {
							if (c instanceof SlashCommand slashCommand && !slashCommand.isHidden(userSettings))
								commandsInCategory.append(", ").append(c.getName());
						}
					} else logger.error("Category without commands: '{}'", category);
				}
				if (commandsInCategory.length() <= 2) {
					continue;
				}
				final String categoryName = Utils.firstLetterUppercase((category != null ? category.name() : cmde.getTranslation("modules")).toLowerCase());
				if (category != null) {
					builder.addField(categoryName, commandsInCategory.substring(1), false);
					buttons.add(Button.primary(category.name(), categoryName));
				} else {
					builder.addField(cmde.getTranslation("modules"), commandsInCategory.substring(1), false);
					buttons.add(Button.success("overview", cmde.getTranslation("modules")));
				}
			}
			buttons.add(Button.danger("close", cmde.getTranslation("close")));
			channel.sendMessageEmbeds(builder.build())
				.setComponents(Lists.partition(buttons, 5)
					.stream()
					.map(ActionRow::of)
					.toList())
				.queue(msg -> this.waitForHelpCatSelection(msg, member, userSettings, cmde));
		}
	}

	private void waitForHelpCatSelection(final @NotNull Message msg, final @NotNull Member author, final UserSettings userSettings, final @NotNull SlashCommandEvent cmde) {
		Bot.getInstance().getEventWaiter().waitForEvent(ButtonInteractionEvent.class, event -> msg.getChannel().asTextChannel().getIdLong() == event.getChannel().getIdLong() && msg.getIdLong() == event.getMessageIdLong() && !event.getUser().isBot() && event.getUser().getIdLong() == author.getIdLong(), event -> {
			final Button button = event.getButton();

			final EmbedBuilder builder = cmde.success().setDescription(cmde.getTranslation("onlyexecutorcancontrol"));

			if (Objects.equals(button.getId(), "overview")) {
				builder.setTitle(cmde.getTranslation("help") + " | " + cmde.getTranslation("modules"));

				final Category[] cats = Category.values();
				for (int i = 0; i <= cats.length; i++) {
					StringBuilder commandsInCategory = new StringBuilder();
					Category category = null;
					if (i == 0) {
						commandsInCategory = new StringBuilder(", " + cmde.getTranslationOrEmpty("helpmodules"));
					} else {
						category = cats[i - 1];
						for (final Command c : slashCommandBase.getCommandsInCategory().get(category)) {
							if (c instanceof SlashCommand slashCommand && !slashCommand.isHidden(userSettings))
								commandsInCategory.append(", ").append(c.getName());
						}
					}

					if (commandsInCategory.length() <= 2) {
						continue;
					}
					builder.addField(Utils.firstLetterUppercase((category != null ? category.name() : cmde.getTranslation("modules")).toLowerCase()), commandsInCategory.substring(1), false);
				}
			} else if (Objects.equals(button.getId(), "close")) {
				msg.delete().queue();
				return;
			} else {
				final Category category = Category.valueOf(button.getId());
				builder.setTitle(cmde.getTranslation("help") + " | " + category.name().toUpperCase());
				for (final Map.Entry<String, Pair<Long, Command>> entry : slashCommandBase.getCommands().entrySet()) {
					var command = entry.getValue().getSecond();
					if (command instanceof SlashCommand cmd && !cmd.isHidden(userSettings) && (command.getCategory() == category)) {
						if (command.getProgress() != Progress.DONE) continue;
						builder.addField(cmde.getCommandHelp(cmd), cmde.getTranslationOrEmpty("help" + command.getName()), false);
					}
				}


				for (final Progress pr : Progress.values()) {
					if (pr == Progress.DONE) {
						continue;
					}
					for (final Map.Entry<String, Pair<Long, Command>> entry : slashCommandBase.getCommands().entrySet()) {
						var cmd = entry.getValue().getSecond();
						if (!(cmd instanceof SlashCommand slashCommand) || slashCommand.isHidden(userSettings) || (cmd.getCategory() != category) || (cmd.getProgress() != pr)) {
							continue;
						}

						if (!slashCommand.isHidden(userSettings) && (slashCommand.getCategory() == category) && slashCommand.getProgress() == pr) {
							final String commandHelp = cmde.getTranslation("help" + slashCommand.getName().toLowerCase());
							if (commandHelp == null) {
								logger.error("Help for '{}' not set!", entry.getKey());
							}
							builder.addField(pr.name().toUpperCase() + ": " + cmde.getCommandHelp(slashCommand), cmde.getTranslationOrEmpty("help" + slashCommand.getName()), false);
						}
					}
				}
			}


			event.editMessageEmbeds(builder.build()).queue();
			this.waitForHelpCatSelection(msg, author, userSettings, cmde);
		}, 5, TimeUnit.MINUTES, () -> msg.editMessage(cmde.getTranslation("helpmenuexpired", new Placeholder("cmd", "/" + this.getName()))).setEmbeds().setComponents().queue());
	}
}