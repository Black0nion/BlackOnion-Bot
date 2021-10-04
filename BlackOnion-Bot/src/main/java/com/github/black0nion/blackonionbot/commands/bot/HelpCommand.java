package com.github.black0nion.blackonionbot.commands.bot;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.bot.SlashCommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.Progress;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.google.common.collect.Lists;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

public class HelpCommand extends SlashCommand {

    public HelpCommand() {
	final OptionData optionData = new OptionData(OptionType.STRING, "category", "The category to show the commands of");
	Arrays.asList(Category.values()).forEach(l -> optionData.addChoice(l.name(), l.name().toLowerCase()));
	this.setData(new CommandData("help", "Shows a overview of all commands")
		.addOption(OptionType.STRING, "command", "The command to show the informations of")
		.addOptions(optionData))
	.notToggleable();
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	try {
	    if (e.getOption("command") != null) {
		// a command
		final String cmdName = e.getOption("command").getAsString();
		for (final Map.Entry<String[], Command> entry : CommandBase.commandsArray.entrySet()) {
		    final Command cmd = entry.getValue();
		    if (cmd.isVisible(author) && Arrays.asList(entry.getKey()).contains(cmdName)) {
			cmde.success("help", CommandEvent.getCommandHelp(guild, author, cmd), cmde.getTranslationOrEmpty("help" + cmd.getCommand()[0].toLowerCase()));
			return;
		    }
		}

		for (final Entry<Category, List<SlashCommand>> entry : SlashCommandBase.commandsInCategory.entrySet()) {
		    for (final SlashCommand cmd : entry.getValue()) {
			if (cmd.isVisible(author) && cmd.getData().getName().equalsIgnoreCase(cmdName)) {
			    cmde.success("help", SlashCommandExecutedEvent.getCommandHelp(guild, author, cmd), cmde.getTranslationOrEmpty("help" + cmd.getData().getName().toLowerCase()));
			    return;
			}
		    }
		}

		cmde.error("commandnotfound", "thecommandnotfound", new Placeholder("command", "`" + cmdName + "`"));
	    } else if (e.getOption("category") != null) {
		final Category category = Category.valueOf(e.getOption("category").getAsString());
		final EmbedBuilder builder = cmde.success().setTitle(cmde.getTranslation("help") + " | " + category.name());
		for (final Command c : CommandBase.commandsInCategory.get(category)) {
		    if (c.isVisible(author)) {
			builder.addField(CommandEvent.getCommandHelp(guild, author, c), cmde.getTranslationOrEmpty("help" + c.getCommand()[0]), false);
		    }
		}
		for (final SlashCommand c : SlashCommandBase.commandsInCategory.get(category)) {
		    if (c.isVisible(author)) {
			builder.addField(SlashCommandExecutedEvent.getCommandHelp(guild, author, c), cmde.getTranslationOrEmpty("help" + c.getData().getName().toLowerCase()), false);
		    }
		}
		cmde.reply(builder);
	    } else {
		// start the help system thingy lmao
		final EmbedBuilder builder = cmde.success().setTitle(cmde.getTranslation("help") + " | " + cmde.getTranslation("modules")).setDescription(cmde.getTranslation("onlyexecutorcancontrol"));

		final Category[] cats = Category.values();
		final List<Button> buttons = new LinkedList<>();
		for (int i = 0; i <= cats.length; i++) {
		    String commandsInCategory = "";
		    Category category = null;
		    if (i == 0) {
			commandsInCategory = ", " + cmde.getTranslation("helpmodules");
		    } else {
			category = cats[i - 1];
			for (final Command c : CommandBase.commandsInCategory.get(category)) {
			    if (c.isVisible(author)) {
				commandsInCategory += ", " + c.getCommand()[0];
			    }
			}
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
		e.replyEmbeds(builder.build()).addActionRows(Lists.partition(buttons, 5).stream().map(ActionRow::of).collect(Collectors.toList())).flatMap(InteractionHook::retrieveOriginal).queue(msg -> {
		    this.waitForHelpCatSelection(msg, member, cmde);
		});
	    }
	} catch (final Exception ex) {
	    // sum stupid exception bruh
	    if (!(ex instanceof IllegalArgumentException)) {
		ex.printStackTrace();
	    } else {
		ex.printStackTrace();
		cmde.error("some error happend wtf", "how da fuq did that happen?\n" + ex.getMessage());
	    }
	}
    }

    private final void waitForHelpCatSelection(final Message msg, final BlackMember author, final SlashCommandExecutedEvent cmde) {
	CommandBase.waiter.waitForEvent(ButtonClickEvent.class, event -> msg.getTextChannel().getIdLong() == event.getChannel().getIdLong() && msg.getIdLong() == event.getMessageIdLong() && !event.getUser().isBot() && event.getUser().getIdLong() == author.getIdLong(), event -> {
	    final Button button = event.getButton();

	    final BlackGuild guild = cmde.getGuild();
	    final BlackUser user = cmde.getUser();

	    final EmbedBuilder builder = cmde.success().setDescription(LanguageSystem.getTranslation("onlyexecutorcancontrol", user, guild));

	    if (button.getId().equals("overview")) {
		builder.setTitle(LanguageSystem.getTranslation("help", user, guild) + " | " + LanguageSystem.getTranslation("modules", user, guild));

		final Category[] cats = Category.values();
		for (int i = 0; i <= cats.length; i++) {
		    String commandsInCategory = "";
		    Category category = null;
		    if (i == 0) {
			commandsInCategory = ", " + cmde.getTranslationOrEmpty("helpmodules");
		    } else {
			category = cats[i - 1];
			for (final Command c : CommandBase.commandsInCategory.get(category)) {
			    if (c.isVisible(user)) {
				commandsInCategory += ", " + c.getCommand()[0];
			    }
			}
		    }

		    if (commandsInCategory.length() <= 2) {
			continue;
		    }
		    builder.addField(Utils.firstLetterUppercase((category != null ? category.name() : LanguageSystem.getTranslation("modules", user, guild)).toLowerCase()), commandsInCategory.substring(1), false);
		}
	    } else if (button.getId().equals("close")) {
		msg.delete().queue();
		return;
	    } else {
		final Category category = Category.valueOf(button.getId());
		builder.setTitle(LanguageSystem.getTranslation("help", user, guild) + " | " + category.name().toUpperCase());
		for (final Map.Entry<String[], Command> entry : CommandBase.commandsArray.entrySet()) if (entry.getValue().isVisible(user) && (entry.getValue().getCategory() == category)) if (entry.getValue().getProgress() == Progress.DONE) {
		    builder.addField(CommandEvent.getCommandHelp(guild, user, entry.getValue()), cmde.getTranslationOrEmpty("help" + entry.getValue().getCommand()[0]), false);
		}

		for (final Progress pr : Progress.values()) {
		    if (pr == Progress.DONE) {
			continue;
		    }
		    for (final Map.Entry<String[], Command> entry : CommandBase.commandsArray.entrySet()) {
			final Command command = entry.getValue();
			if (command.isVisible(user) && (command.getCategory() == category) && command.getProgress() == pr) {
			    final String commandHelp = cmde.getTranslation("help" + entry.getValue().getCommand()[0].toLowerCase());
			    if (commandHelp == null) {
				System.out.println("Help for " + entry.getKey()[0] + " not set!");
			    }
			    builder.addField(pr.name().toUpperCase() + ": " + CommandEvent.getCommandHelp(guild, user, entry.getValue()), cmde.getTranslationOrEmpty("help" + entry.getValue().getCommand()[0]), false);
			}
		    }
		}
	    }

	    event.editMessageEmbeds(builder.build()).queue();
	    this.waitForHelpCatSelection(msg, author, cmde);
	}, 5, TimeUnit.MINUTES, () -> {
	    msg.editMessage(cmde.getTranslation("helpmenuexpired", new Placeholder("cmd", "/" + this.getData().getName()))).setEmbeds().setActionRows().queue();
	});
    }
}