package com.github.black0nion.blackonionbot.commands.fun;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author _SIM_
 */
public class HangmanCommand extends SlashCommand {

    private static final HashMap<Language, List<String>> hangmanWords = new HashMap<>();

    public static final List<Long> ingamePlayers = new ArrayList<>();

    public HangmanCommand() {
        super("Hangman", "Used to play hangman");
        for (final Language language : LanguageSystem.getLanguages().values()) {
            final String translation = language.getTranslation("hangmanwords");
            if (translation != null) {
                hangmanWords.put(language, Arrays.asList(translation.toLowerCase().split(",")));
            }
        }
    }

    @Override
    public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, @NotNull BlackUser author, BlackGuild guild, TextChannel channel) {
        if (ingamePlayers.contains(author.getIdLong())) {
            cmde.error("alreadyingame", "nomultitasking");
            return;
        }

        final List<String> wordsInThisLang = hangmanWords.get(cmde.getLanguage());
        final String solution = wordsInThisLang.get(ThreadLocalRandom.current().nextInt(wordsInThisLang.size()));
        cmde.reply(cmde.success().setTitle("hangman"), msg -> {
            ingamePlayers.add(author.getIdLong());
            rerun(e, cmde, solution, new ArrayList<>());
        });
    }

    private static void rerun(SlashCommandInteractionEvent e, final SlashCommandEvent cmde, final String solution, final List<Character> alreadyGuessed) {
        final String failedAttempts = getFailedAttempts(solution, alreadyGuessed);
        final int failedAttemptsCount = failedAttempts.equalsIgnoreCase("") ? 0 : failedAttempts.split(", ").length;

        if (failedAttemptsCount >= 7) {
            e.getMessageChannel().retrieveMessageById(e.getTextChannel().getLatestMessageId()).queue(msg -> {
                msg.editMessageEmbeds(cmde.error().setTitle("hangman").addField("urded", "notbigsurprise", false).build()).queue();
            });
            ingamePlayers.remove(cmde.getUser().getIdLong());
            return;
        }

        final EmbedBuilder builder = cmde.success().setTitle("hangman").setDescription("```\n" + getSpacesString(solution, alreadyGuessed) + "\nFailed Attempts: " + failedAttempts + "\n" + getDrawing(failedAttemptsCount) + "```");
        if (won(solution, alreadyGuessed)) {
            e.getMessageChannel().retrieveMessageById(e.getTextChannel().getLatestMessageId()).queue(msg -> {
                msg.editMessageEmbeds(cmde.success().setTitle("hangman").addField("uwon", "bigsurprise", false).build()).queue();
            });
            ingamePlayers.remove(cmde.getUser().getIdLong());
            return;
        }
        e.getMessageChannel().retrieveMessageById(e.getTextChannel().getLatestMessageId()).queue(msg -> {
            msg.editMessageEmbeds(builder.build()).queue(message -> Bot.getInstance().getEventWaiter().waitForEvent(
                    MessageReceivedEvent.class,
                    event -> event.getChannelType() == ChannelType.TEXT && event.getGuild().getIdLong() == cmde.getGuild().getIdLong() && event.getAuthor().getIdLong() == cmde.getUser().getIdLong() && !event.getMessage().getContentRaw().toLowerCase().startsWith("!") && !alreadyGuessed.contains(event.getMessage().getContentRaw().toLowerCase().charAt(0)),
                    event -> {
                        if (event.getMessage().getContentRaw().equalsIgnoreCase(solution)) {
                            cmde.reply(cmde.success().setTitle("hangman").addField("uwon", "bigsurprise", false));
                            ingamePlayers.remove(cmde.getUser().getIdLong());
                            return;
                        }
                        alreadyGuessed.add(event.getMessage().getContentRaw().toLowerCase().charAt(0));
                        rerun(e, cmde, solution, alreadyGuessed);
                    }, 1, TimeUnit.MINUTES, () -> {
                        cmde.reply(cmde.success().addField("timeout", "tooktoolong", false));
                        ingamePlayers.remove(cmde.getUser().getIdLong());
                    }
            ));
        });
    }

    private static String getDrawing(final int tries) {
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

    private static String getSpacesString(final String solution, final List<Character> tries) {
        StringBuilder s = new StringBuilder("Word: ");

        for (final Character c : solution.toCharArray())
            if (tries.contains(c)) {
                s.append(c).append(" ");
            } else {
                s.append("_ ");
            }
        return s.toString();
    }

    private static String getFailedAttempts(final String solution, final List<Character> tries) {
        StringBuilder failedAttempts = new StringBuilder();
        for (final Character c : tries)
            if (!solution.contains(String.valueOf(c))) {
                failedAttempts.append(", ").append(c);
            }
        return failedAttempts.toString().equalsIgnoreCase("") ? "" : failedAttempts.substring(1);
    }

    private static boolean won(final String solution, final List<Character> tries) {
        for (final Character c : solution.toCharArray()) if (!tries.contains(c)) return false;
        return true;
    }

    @Reloadable("hangman")
    public static void reload() {
        ingamePlayers.clear();
    }
}