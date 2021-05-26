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
		this.setCommand("help")
			.notToggleable();
	}
	
	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		try {
			if (args.length >= 2) {
				// a command
				for (Map.Entry<String[], Command> entry : CommandBase.commandsArray.entrySet()) {
					if (entry.getValue().isVisible() && Arrays.asList(entry.getKey()).contains(args[1])) {
						final String commandHelp = LanguageSystem.getTranslation("help" + entry.getValue().getCommand()[0].toLowerCase(), author, guild);
						if (commandHelp == null) System.out.println("Help for " + entry.getKey()[0] + " not set!");
						cmde.success("help", CommandEvent.getCommandHelp(guild, author, entry.getValue()), commandHelp != null ? commandHelp : "empty");
						return;
					}
				}
				cmde.error("commandnotfound", "thecommandnotfound", new Placeholder("command", "`" + args[1] + "`"));
			} else {
				// start the help system thingy lmao
				EmbedBuilder builder = cmde.success()
						.setTitle(cmde.getTranslation("help") + " | " + cmde.getTranslation("modules"))
						.setDescription(cmde.getTranslation("onlyexecutorcancontrol"));
				
				final Category[] cats = Category.values();
				for (int i = 0; i <= cats.length; i++) {
					String commandsInCategory = "";
					Category c = null;
					if (i == 0) {
						commandsInCategory = ", " + cmde.getTranslation("helpmodules");
					} else {						
						c = cats[i - 1];
						for (Map.Entry<String[], Command> entry : CommandBase.commandsArray.entrySet()) {
							if (entry.getValue().getCategory() == c && entry.getValue().isVisible())
								commandsInCategory += ", " + entry.getValue().getCommand()[0];
						}
					}
					if (commandsInCategory.length() <= 2) continue;
					builder.addField(Utils.emojis[i] + (c != null ? " " + c.name() : " " + cmde.getTranslation("modules")), commandsInCategory.substring(1), false);
				}
				message.reply(builder.build()).queue((msg) -> {
					for (int i = 0; i <= cats.length; i++)
						msg.addReaction(Utils.numbersUnicode.get(i)).queue();
					waitForHelpCatSelection(BlackMessage.from(msg), member, cats.length+1);
				});
			}
		} catch (Exception ex) {
			// sum stupid exception bruh
			if (!(ex instanceof IllegalArgumentException)) {
				ex.printStackTrace();
			} else {
				ex.printStackTrace();
				message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("What just happend?", 
						"hau did u do that???", false).build()).queue();
			}
		}
	}
	
	private final void waitForHelpCatSelection(BlackMessage msg, BlackMember author, int catCount) {
		CommandBase.waiter.waitForEvent(MessageReactionAddEvent.class,
				(event) -> msg.getIdLong() == event.getMessageIdLong() && !event.getUser().isBot() && event.getUserIdLong() == author.getIdLong(), 
				(event) -> {
					event.getReaction().removeReaction(event.getUser()).queue();
					Integer emojiReactionNum = Utils.numbersUnicode.entrySet().stream().filter((entry) -> {return entry.getValue().equals(event.getReactionEmote().getAsCodepoints());}).findFirst().get().getKey();
					
					if (!event.getReactionEmote().isEmoji() || !Utils.numbersUnicode.containsValue(event.getReactionEmote().getAsCodepoints()) || catCount < emojiReactionNum)
						waitForHelpCatSelection(msg, author, catCount);
					
					final BlackGuild guild = msg.getBlackGuild();
					final BlackUser user = author.getBlackUser();
					
					EmbedBuilder builder = EmbedUtils.getSuccessEmbed(user, guild)
							.setDescription(LanguageSystem.getTranslation("onlyexecutorcancontrol", user, guild));
					
					if (emojiReactionNum == 0) {
						builder.setTitle(LanguageSystem.getTranslation("help", user, guild) + " | " + LanguageSystem.getTranslation("modules", user, guild));

						final Category[] cats = Category.values();
						for (int i = 0; i <= cats.length; i++) {
							String commandsInCategory = "";
							Category c = null;
							if (i == 0) {
								commandsInCategory = ", " + LanguageSystem.getTranslation("helpmodules", user, guild);
							} else {						
								c = cats[i - 1];
								for (Map.Entry<String[], Command> entry : CommandBase.commandsArray.entrySet()) {
									if (entry.getValue().getCategory() == c && entry.getValue().isVisible())
										commandsInCategory += ", " + entry.getValue().getCommand()[0];
								}
							}
							
							builder.addField(Utils.emojis[i] + (c != null ? " " + c.name() : " " + LanguageSystem.getTranslation("modules", user, guild)), commandsInCategory.substring(1), false);
						}
					} else {
						emojiReactionNum--;
						final Category category = Category.values()[emojiReactionNum];
						builder.setTitle(LanguageSystem.getTranslation("help", user, guild) + " | " + category.name().toUpperCase());
						for (Map.Entry<String[], Command> entry : CommandBase.commandsArray.entrySet()) {
							if (entry.getValue().isVisible() && (entry.getValue().getCategory() == category)) {
								if (entry.getValue().getProgress() == Progress.DONE) {
									final String commandHelp = LanguageSystem.getTranslation("help" + entry.getValue().getCommand()[0].toLowerCase(), user, guild);
									if (commandHelp == null) System.out.println("Help for " + entry.getKey()[0] + " not set!");
									builder.addField(CommandEvent.getCommandHelp(guild, user, entry.getValue()), commandHelp != null ? commandHelp : "empty", false);
								}
							}
						}
						
						for (Progress pr : Progress.values()) {
							if (pr == Progress.DONE)
								continue;
							for (Map.Entry<String[], Command> entry : CommandBase.commandsArray.entrySet()) {
								Command command = entry.getValue();
								if (command.isVisible() && (command.getCategory() == category) && command.getProgress() == pr) {
									final String commandHelp = LanguageSystem.getTranslation("help" + entry.getValue().getCommand()[0].toLowerCase(), user, guild);
									if (commandHelp == null) System.out.println("Help for " + entry.getKey()[0] + " not set!");
									builder.addField(pr.name().toUpperCase() + ": " + CommandEvent.getCommandHelp(guild, user, entry.getValue()), commandHelp != null ? commandHelp : "empty", false);
								}
							}
						}
					}
					
					msg.editMessage(builder.build()).queue();
					waitForHelpCatSelection(msg, author, catCount);
		}, 5, TimeUnit.MINUTES, () -> {msg.delete().queue();});
	}
}