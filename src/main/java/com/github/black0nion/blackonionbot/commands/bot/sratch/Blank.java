package com.github.black0nion.blackonionbot.commands.bot.sratch;

import com.github.black0nion.blackonionbot.bot.SlashCommandBase;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.google.common.collect.Lists;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Blank {
    public static void main(String[] args) {
        try {
            var t = e.getTextChannel().retrieveMessageById(e.getChannel().getLatestMessageId());

            t.queue(message -> {
                if (message.getEmbeds().size() > 0) {
                    message.delete().queue();
                } else {
                    e.getChannel().sendMessage(message.getContentRaw()).queue();
                }
            });


            if (e.getOptions().size() >= 2) {
                // a command
                for (final Map.Entry<String[], SlashCommand> entry : SlashCommandBase.commands.entrySet()) {
                    final SlashCommand cmd = entry.getValue();
                    if (cmd.isHidden(author) && Arrays.asList(entry.getKey()).contains(args[1])) {
                        cmde.success("help", SlashCommandEvent.getCommandHelp(cmd), cmde.getTranslationOrEmpty("help" + cmd.getName().toLowerCase()));
                        return;
                    }
                }

                final Category category = Category.parse(args[1]);
                if (category != null) {
                    final EmbedBuilder builder = cmde.success().setTitle(cmde.getTranslation("help") + " | " + category.name());
                    for (final SlashCommand c : SlashCommandBase.commandsInCategory.get(category)) {
                        builder.addField(SlashCommandEvent.getCommandHelp(c), cmde.getTranslationOrEmpty("help" + c.getName()), false);
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
                        if (SlashCommandBase.commandsInCategory.containsKey(category)) {
                            for (final SlashCommand c : SlashCommandBase.commandsInCategory.get(category)) {
                                if (c.isHidden(author)) {
                                    commandsInCategory.append(", ").append(c.getName());
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
                        .queue(msg -> this.waitForHelpCatSelection(member, cmde, e, channel));
            }
        } catch (final Exception ex) {
            // sum stupid exception bruh
            if (!(ex instanceof IllegalArgumentException)) {
                ex.printStackTrace();
            } else {
                ex.printStackTrace();
                e.replyEmbeds(cmde.error().addField("What just happend?", "how, just how", false).build()).queue();
            }
        }
    }
}
