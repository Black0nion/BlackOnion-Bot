package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.TextCommand;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.Progress;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.google.common.collect.Lists;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class HelpCommand extends TextCommand {

	public HelpCommand() {
		this.setCommand("help").notToggleable();
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		try {
			message.delete().queue();
			if (args.length >= 2) {
				// a command
				for (final Map.Entry<String[], TextCommand> entry : CommandBase.commandsArray.entrySet()) {
					final TextCommand cmd = entry.getValue();
					if (cmd.isVisible(author) && Arrays.asList(entry.getKey()).contains(args[1])) {
						cmde.success("help", CommandEvent.getCommandHelp(guild, cmd), cmde.getTranslationOrEmpty("help" + cmd.getCommand()[0].toLowerCase()));
						return;
					}
				}

				final Category category = Category.parse(args[1]);
				if (category != null) {
					final EmbedBuilder builder = cmde.success().setTitle(cmde.getTranslation("help") + " | " + category.name());
					for (final TextCommand c : CommandBase.commandsInCategory.get(category)) {
						builder.addField(CommandEvent.getCommandHelp(guild, c), cmde.getTranslationOrEmpty("help" + c.getCommand()[0]), false);
					}
					cmde.reply(builder);
				} else {
					cmde.error("commandnotfound", "thecommandnotfound", new Placeholder("command", "`" + args[1] + "`"));
				}
			} else {
				// start the help system thingy lmao
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
						if (CommandBase.commandsInCategory.containsKey(category)) {
							for (final TextCommand c : CommandBase.commandsInCategory.get(category)) {
								if (c.isVisible(author)) {
									commandsInCategory.append(", ").append(c.getCommand()[0]);
								}
							}
						} else System.out.println("wtf:  " + category);
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
						.setActionRows(Lists.partition(buttons, 5)
								.stream()
								.map(ActionRow::of)
								.toList())
						.queue(msg -> this.waitForHelpCatSelection(msg, member, cmde));
			}
		} catch (final Exception ex) {
			// sum stupid exception bruh
			if (!(ex instanceof IllegalArgumentException)) {
				ex.printStackTrace();
			} else {
				ex.printStackTrace();
				message.replyEmbeds(cmde.error().addField("What just happend?", "how, just how", false).build()).queue();
			}
		}
	}

	private void waitForHelpCatSelection(final Message msg, final BlackMember author, final CommandEvent cmde) {
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
		}, 5, TimeUnit.MINUTES, () -> msg.editMessage(cmde.getTranslation("helpmenuexpired", new Placeholder("cmd", cmde.getGuild().getPrefix().getValue() + this.getCommand()[0]))).setEmbeds().setActionRows().queue());
	}
}