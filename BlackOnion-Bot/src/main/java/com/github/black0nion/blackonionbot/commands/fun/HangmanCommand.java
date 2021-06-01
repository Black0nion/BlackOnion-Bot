/**
 * 
 */
package com.github.black0nion.blackonionbot.commands.fun;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * @author _SIM_
 */
public class HangmanCommand extends Command {
	
	private static final HashMap<Language, List<String>> hangmanWords = new HashMap<>();
	
	public HangmanCommand() {
		this.setCommand("hangman");
		for (Language language : LanguageSystem.getLanguages().values()) {
			final String translation = language.getTranslation("hangmanwords");
			if (translation != null)
				hangmanWords.put(language, Arrays.asList(translation.toLowerCase().split(",")));
		}
	}
	
	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		final List<String> wordsInThisLang = hangmanWords.get(cmde.getLanguage());
		final String solution = wordsInThisLang.get(Bot.random.nextInt(wordsInThisLang.size()));
		cmde.reply(cmde.success().setTitle("hangman"), msg -> {
			rerun(msg, cmde, solution, new ArrayList<>());
			return;
		});
	}
	
	private static void rerun(Message msg, CommandEvent cmde, String solution, List<Character> alreadyGuessed) {
		final String failedAttempts = getFailedAttempts(solution, alreadyGuessed);
		final int failedAttemptsCount = failedAttempts.equalsIgnoreCase("") ? 0 : failedAttempts.split(", ").length;
		
		if (failedAttemptsCount >= 7) {
			msg.editMessage(cmde.error().setTitle("hangman").addField("urded", "notbigsurprise", false).build()).queue();
			return;
		}
		
		EmbedBuilder builder = cmde.success().setTitle("hangman").setDescription("```\n" + getSpacesString(solution, alreadyGuessed) + "\nFailed Attempts: " + failedAttempts + "\n" + getDrawing(failedAttemptsCount) + "```");
		if (won(solution, alreadyGuessed)) {
			msg.editMessage(cmde.success().setTitle("hangman").addField("uwon", "bigsurprise", false).build()).queue();
			return;
		}
		
		msg.editMessage(builder.build()).queue(message -> {
			CommandBase.waiter.waitForEvent(
				GuildMessageReceivedEvent.class, 
				event -> event.getGuild().getIdLong() == cmde.getGuild().getIdLong() && event.getAuthor().getIdLong() == cmde.getUser().getIdLong() && !event.getMessage().getContentRaw().toLowerCase().startsWith("!") && !alreadyGuessed.contains(event.getMessage().getContentRaw().toLowerCase().charAt(0)), 
				event -> {
					alreadyGuessed.add(event.getMessage().getContentRaw().toLowerCase().charAt(0));
					rerun(msg, cmde, solution, alreadyGuessed);
					return;
				}, 1, TimeUnit.MINUTES, () -> msg.editMessage(cmde.success().addField("timeout", "tooktoolong", false).build()).queue());
		});
	}
	
	private static String getDrawing(int tries) {
		String result = "";
		if (tries >= 1) {
			result += "    O\n";
			if (tries >= 2) {
				result += "   \\";
				if (tries >= 3) {
					result += "|";
					if (tries >= 4) {
						result += "/\n";
						if (tries >= 5) {
							result += "    |\n";
							if (tries >= 6) {
								result += "   /";
								if (tries >= 7) {
									result += " \\";
								}
							}
						}
					}
				}
			}
		}
		return result;
	}
	
	private static String getSpacesString(String solution, List<Character> tries) {
		String s = "Word: ";
		
		for (Character c : solution.toCharArray()) {
			if (tries.contains(c)) s += String.valueOf(c) + " ";
			else s += "_ ";
		}
		return s;
	}
	
	private static String getFailedAttempts(String solution, List<Character> tries) {
		String failedAttempts = "";
		for (Character c : tries) {
			if (!solution.contains(String.valueOf(c))) failedAttempts += ", " + c;
		}
		return failedAttempts.equalsIgnoreCase("") ? "" : failedAttempts.substring(1);
	}
	
	private static boolean won(String solution, List<Character> tries) {
		for (Character c : solution.toCharArray()) {
			if (!tries.contains(c)) return false;
		}
		return true;
	}
}