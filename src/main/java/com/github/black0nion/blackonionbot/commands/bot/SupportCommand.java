package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.commands.TextCommand;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.stream.Collectors;

import static com.github.black0nion.blackonionbot.utils.config.Config.metadata;

public class SupportCommand extends TextCommand {

  public SupportCommand() {
    this.setCommand("support", "authors");
  }

  private static final String bot_developers = metadata.authors().entrySet().stream()
      .map(entry -> "[" + entry.getKey() + "](" + entry.getValue() + ")")
      .collect(Collectors.joining(", "));
  private static final String blackonion_authors = metadata.blackonion_authors().entrySet().stream()
      .map(entry -> "[" + entry.getKey() + "](" + entry.getValue() + ")")
      .collect(Collectors.joining(", "));

  @Override
  public void execute(final String[] args, final CommandEvent cmde,
      final MessageReceivedEvent event, final Message message, final BlackMember member,
      final BlackUser author, final BlackGuild guild, final TextChannel channel) {
    cmde.reply(cmde.success()
        .addField("Get help", "[Discord Server](https://discord.gg/ZzGKy9RCBY)", false)
        .addField("Bot Developers", bot_developers, false)
        .addField("BlackOnion Authors", blackonion_authors, false));
  }
}
