package com.github.black0nion.blackonionbot.commands.bot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.CommandVisibility;
import com.github.black0nion.blackonionbot.misc.Progress;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class HelpCommand implements Command {

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, TextChannel channel) {
		try {
			if (args.length >= 2) {
				// a command
				for (Map.Entry<String[], Command> entry : CommandBase.commandsArray.entrySet()) {
					if (entry.getValue().getVisisbility() == CommandVisibility.SHOWN && new ArrayList<String>(Arrays.asList(entry.getKey())).contains(args[1])) {
						final String commandHelp = LanguageSystem.getTranslatedString("help" + entry.getValue().getCommand()[0].toLowerCase(), e.getAuthor(), e.getGuild());
						if (commandHelp == null) System.out.println("Help for " + entry.getKey()[0] + " not set!");
						channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild).setTitle("help").addField(Utils.getCommandHelp(guild, author, entry.getValue()), commandHelp != null ? commandHelp : "empty", false).build()).queue();
						return;
					}
				}
				channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("commandnotfound", LanguageSystem.getTranslatedString("thecommandnotfound", author, guild).replace("%command%", "`" + args[1] + "`"), false).build()).queue();
			} else {
				// start the help system thingy lmao
				EmbedBuilder builder = EmbedUtils.getSuccessEmbed(author, guild)
						.setTitle(LanguageSystem.getTranslatedString("help", author, guild) + " | " + LanguageSystem.getTranslatedString("modules", author, guild))
						.setDescription(LanguageSystem.getTranslatedString("onlyexecutorcancontrol", author, guild));
				
				final Category[] cats = Category.values();
				for (int i = 0; i <= cats.length; i++) {
					String commandsInCategory = "";
					Category c = null;
					if (i == 0) {
						commandsInCategory = ", " + LanguageSystem.getTranslatedString("helpmodules", author, guild);
					} else {						
						c = cats[i - 1];
						for (Map.Entry<String[], Command> entry : CommandBase.commandsArray.entrySet()) {
							if (entry.getValue().getCategory() == c && entry.getValue().getVisisbility() == CommandVisibility.SHOWN)
								commandsInCategory += ", " + entry.getValue().getCommand()[0];
						}
					}
					if (commandsInCategory.length() <= 2) continue;
					builder.addField(Utils.emojis[i] + (c != null ? " " + c.name() : " " + LanguageSystem.getTranslatedString("modules", author, guild)), commandsInCategory.substring(1), false);
				}
				channel.sendMessage(builder.build()).queue((msg) -> {
					for (int i = 0; i <= cats.length; i++)
						msg.addReaction(Utils.numbersUnicode.get(i)).queue();
					waitForHelpCatSelection(msg, member, cats.length+1);
				});
			}
		} catch (Exception ex) {
			// sum stupid exception bruh
			if (!(ex instanceof IllegalArgumentException)) {
				ex.printStackTrace();
			} else {
				ex.printStackTrace();
				channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("What just happend?", 
						"hau did u do that???", false).build()).queue();
			}
		}
	}
	
	private static final void waitForHelpCatSelection(Message msg, Member author, int catCount) {
		CommandBase.waiter.waitForEvent(MessageReactionAddEvent.class, 
				(event) -> msg.getIdLong() == event.getMessageIdLong() && !event.getUser().isBot() && event.getUserIdLong() == author.getIdLong(), 
				(event) -> {
					event.getReaction().removeReaction(event.getUser()).queue();
					Integer emojiReactionNum = Utils.numbersUnicode.entrySet().stream().filter((entry) -> {return entry.getValue().equals(event.getReactionEmote().getAsCodepoints());}).findFirst().get().getKey();
					
					if (!event.getReactionEmote().isEmoji() || !Utils.numbersUnicode.containsValue(event.getReactionEmote().getAsCodepoints()) || catCount < emojiReactionNum)
						waitForHelpCatSelection(msg, author, catCount);
					
					final Guild guild = msg.getGuild();
					final User user = author.getUser();
					
					EmbedBuilder builder = EmbedUtils.getSuccessEmbed(user, guild)
							.setDescription(LanguageSystem.getTranslatedString("onlyexecutorcancontrol", author.getUser(), guild));
					
					if (emojiReactionNum == 0) {
						builder.setTitle(LanguageSystem.getTranslatedString("help", user, guild) + " | " + LanguageSystem.getTranslatedString("modules", user, guild));

						final Category[] cats = Category.values();
						for (int i = 0; i <= cats.length; i++) {
							String commandsInCategory = "";
							Category c = null;
							if (i == 0) {
								commandsInCategory = ", " + LanguageSystem.getTranslatedString("helpmodules", user, guild);
							} else {						
								c = cats[i - 1];
								for (Map.Entry<String[], Command> entry : CommandBase.commandsArray.entrySet()) {
									if (entry.getValue().getCategory() == c && entry.getValue().getVisisbility() == CommandVisibility.SHOWN)
										commandsInCategory += ", " + entry.getValue().getCommand()[0];
								}
							}
							
							builder.addField(Utils.emojis[i] + (c != null ? " " + c.name() : " " + LanguageSystem.getTranslatedString("modules", user, guild)), commandsInCategory.substring(1), false);
						}
					} else {
						emojiReactionNum--;
						final Category category = Category.values()[emojiReactionNum];
						builder.setTitle(LanguageSystem.getTranslatedString("help", user, guild) + " | " + category.name().toUpperCase());
						for (Map.Entry<String[], Command> entry : CommandBase.commandsArray.entrySet()) {
							if (entry.getValue().getVisisbility() == CommandVisibility.SHOWN && (entry.getValue().getCategory() == category)) {
								if (entry.getValue().getProgress() == Progress.DONE) {
									final String commandHelp = LanguageSystem.getTranslatedString("help" + entry.getValue().getCommand()[0].toLowerCase(), user, guild);
									if (commandHelp == null) System.out.println("Help for " + entry.getKey()[0] + " not set!");
									builder.addField(Utils.getCommandHelp(guild, user, entry.getValue()), commandHelp != null ? commandHelp : "empty", false);
								}
							}
						}
						
						for (Progress pr : Progress.values()) {
							if (pr == Progress.DONE)
								continue;
							for (Map.Entry<String[], Command> entry : CommandBase.commandsArray.entrySet()) {
								Command command = entry.getValue();
								if (command.getVisisbility() == CommandVisibility.SHOWN && (command.getCategory() == category) && command.getProgress() == pr) {
									final String commandHelp = LanguageSystem.getTranslatedString("help" + entry.getValue().getCommand()[0].toLowerCase(), user, guild);
									if (commandHelp == null) System.out.println("Help for " + entry.getKey()[0] + " not set!");
									builder.addField(pr.name().toUpperCase() + ": " + Utils.getCommandHelp(guild, user, entry.getValue()), commandHelp != null ? commandHelp : "empty", false);
								}
							}
						}
					}
					
					msg.editMessage(builder.build()).queue();
					waitForHelpCatSelection(msg, author, catCount);
		}, 5, TimeUnit.MINUTES, () -> {msg.delete().queue();});
	}
	
	@Override
	public Category getCategory() {
		return Category.BOT;
	}
	
	@Override
	public String[] getCommand() {
		return new String[] {"help"};
	}

	@Override
	public boolean isToggleable() {
		return false;
	}
}
