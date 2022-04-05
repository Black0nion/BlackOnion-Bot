package com.github.black0nion.blackonionbot.commands.misc;

import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.black0nion.blackonionbot.commands.TextCommand;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.Utils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PollCommand extends TextCommand {

  public PollCommand() {
    this.setCommand("poll", "survey").setSyntax("yes / no question").setRequiredArgumentCount(1);
  }

  @Override
  public String[] getCommand() {
    return new String[] {"poll", "survey"};
  }

  @Override
  public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e,
      final Message message, final BlackMember member, final BlackUser author,
      final BlackGuild guild, final TextChannel channel) {
    cmde.success("poll", String.join(" ", Utils.removeFirstArg(args)), "polltutorial", msg -> {
      msg.addReaction("tick:822036832422068225").queue();
      msg.addReaction("cross:822036805117018132").queue();
    });
  }
}
