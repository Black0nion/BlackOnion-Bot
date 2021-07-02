package com.github.black0nion.blackonionbot.commands.bot;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.Progress;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class HelpCommand extends Command {

    public HelpCommand() {
	this.setCommand("help").notToggleable();
    }

    @Override
    public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final BlackMessage message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	try {
	    if (args.length >= 2) {
		// a command
		for (final Map.Entry<String[], Command> entry : CommandBase.commandsArray.entrySet()) {
		    final Command cmd = entry.getValue();
		    if (cmd.isVisible(author) && Arrays.asList(entry.getKey()).contains(args[1])) {
			cmde.success("help", CommandEvent.getCommandHelp(guild, author, cmd), cmde.getTranslationOrEmpty("help" + cmd.getCommand()[0].toLowerCase()));
			return;
		    }
		}

		final Category category = Category.parse(args[1]);
		if (category != null) {
		    final EmbedBuilder builder = cmde.success().setTitle(cmde.getTranslation("help") + " | " + category.name());
		    for (final Command c : CommandBase.commandsInCategory.get(category)) {
			builder.addField(CommandEvent.getCommandHelp(guild, author, c), cmde.getTranslationOrEmpty("help" + c.getCommand()[0]), false);
		    }
		    cmde.reply(builder);
		} else {
		    cmde.error("commandnotfound", "thecommandnotfound", new Placeholder("command", "`" + args[1] + "`"));
		}
	    } else {
		// start the help system thingy lmao
		final EmbedBuilder builder = cmde.success().setTitle(cmde.getTranslation("help") + " | " + cmde.getTranslation("modules")).setDescription(cmde.getTranslation("onlyexecutorcancontrol"));

		final Category[] cats = Category.values();
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
		    builder.addField(Utils.emojis[i] + (category != null ? " " + category.name() : " " + cmde.getTranslation("modules")), commandsInCategory.substring(1), false);
		}
		message.reply(builder.build()).queue(msg -> {
		    for (int i = 0; i <= cats.length; i++) {
			msg.addReaction(Utils.numbersUnicode.get(i)).queue();
		    }
		    this.waitForHelpCatSelection(BlackMessage.from(msg), member, cats.length + 1);
		});
	    }
	} catch (final Exception ex) {
	    // sum stupid exception bruh
	    if (!(ex instanceof IllegalArgumentException)) {
		ex.printStackTrace();
	    } else {
		ex.printStackTrace();
		message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("What just happend?", "hau did u do that???", false).build()).queue();
	    }
	}
    }

    private final void waitForHelpCatSelection(final BlackMessage msg, final BlackMember author, final int catCount) {
	CommandBase.waiter.waitForEvent(MessageReactionAddEvent.class, event -> msg.getIdLong() == event.getMessageIdLong() && !event.getUser().isBot() && event.getUserIdLong() == author.getIdLong(), event -> {
	    event.getReaction().removeReaction(event.getUser()).queue();
	    Integer emojiReactionNum = Utils.numbersUnicode.entrySet().stream().filter(entry -> entry.getValue().equals(event.getReactionEmote().getAsCodepoints())).findFirst().get().getKey();

	    if (!event.getReactionEmote().isEmoji() || !Utils.numbersUnicode.containsValue(event.getReactionEmote().getAsCodepoints()) || catCount < emojiReactionNum) {
		this.waitForHelpCatSelection(msg, author, catCount);
	    }

	    final BlackGuild guild = msg.getBlackGuild();
	    final BlackUser user = author.getBlackUser();

	    final EmbedBuilder builder = EmbedUtils.getSuccessEmbed(user, guild).setDescription(LanguageSystem.getTranslation("onlyexecutorcancontrol", user, guild));

	    if (emojiReactionNum == 0) {
		builder.setTitle(LanguageSystem.getTranslation("help", user, guild) + " | " + LanguageSystem.getTranslation("modules", user, guild));

		final Category[] cats = Category.values();
		for (int i = 0; i <= cats.length; i++) {
		    String commandsInCategory = "";
		    Category category = null;
		    if (i == 0) {
			commandsInCategory = ", " + LanguageSystem.getTranslation("helpmodules", user, guild);
		    } else {
			category = cats[i - 1];
			for (final Command c : CommandBase.commandsInCategory.get(category)) if (c.isVisible(user)) {
			    commandsInCategory += ", " + c.getCommand()[0];
			}
		    }

		    builder.addField(Utils.emojis[i] + (category != null ? " " + category.name() : " " + LanguageSystem.getTranslation("modules", user, guild)), commandsInCategory.substring(1), false);
		}
	    } else {
		emojiReactionNum--;
		final Category category = Category.values()[emojiReactionNum];
		builder.setTitle(LanguageSystem.getTranslation("help", user, guild) + " | " + category.name().toUpperCase());
		for (final Map.Entry<String[], Command> entry : CommandBase.commandsArray.entrySet()) if (entry.getValue().isVisible(user) && (entry.getValue().getCategory() == category)) if (entry.getValue().getProgress() == Progress.DONE) {
		    final String commandHelp = LanguageSystem.getTranslation("help" + entry.getValue().getCommand()[0].toLowerCase(), user, guild);
		    if (commandHelp == null) {
			System.out.println("Help for " + entry.getKey()[0] + " not set!");
		    }
		    builder.addField(CommandEvent.getCommandHelp(guild, user, entry.getValue()), commandHelp != null ? commandHelp : "empty", false);
		}

		for (final Progress pr : Progress.values()) {
		    if (pr == Progress.DONE) {
			continue;
		    }
		    for (final Map.Entry<String[], Command> entry : CommandBase.commandsArray.entrySet()) {
			final Command command = entry.getValue();
			if (command.isVisible(user) && (command.getCategory() == category) && command.getProgress() == pr) {
			    final String commandHelp = LanguageSystem.getTranslation("help" + entry.getValue().getCommand()[0].toLowerCase(), user, guild);
			    if (commandHelp == null) {
				System.out.println("Help for " + entry.getKey()[0] + " not set!");
			    }
			    builder.addField(pr.name().toUpperCase() + ": " + CommandEvent.getCommandHelp(guild, user, entry.getValue()), commandHelp != null ? commandHelp : "empty", false);
			}
		    }
		}
	    }

	    msg.editMessage(builder.build()).queue();
	    this.waitForHelpCatSelection(msg, author, catCount);
	}, 5, TimeUnit.MINUTES, () -> {
	    msg.delete().queue();
	});
    }
}