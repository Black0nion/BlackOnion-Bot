package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.commands.TextCommand;
import com.github.black0nion.blackonionbot.misc.Warn;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class WarnsCommand extends TextCommand {

  public WarnsCommand() {
    this.setCommand("warns").setSyntax("<@User | UserID>").setRequiredArgumentCount(1)
        .setRequiredPermissions(Permission.KICK_MEMBERS);
  }

  @Override
  public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e,
      final Message message, final BlackMember member, final BlackUser author,
      final BlackGuild guild, final TextChannel channel) {
    final String user = args[1];
    final BlackMember mentionedMember;
    if (Utils.isLong(user)) {
      mentionedMember = BlackMember.from(guild.retrieveMemberById(user).submit().join());
      if (mentionedMember == null) {
        cmde.error("usernotfound", "inputnumber");
        return;
      }
    } else {
      final List<Member> mentionedMembers = message.getMentionedMembers();
      if (mentionedMembers.size() != 0) {
        if (args[1].replace("!", "").equalsIgnoreCase(mentionedMembers.get(0).getAsMention())) {
          mentionedMember = BlackMember.from(mentionedMembers.get(0));
          if (mentionedMember == null) {
            cmde.error("usernotfound", "nousermentioned");
            return;
          }
        } else {
          cmde.sendPleaseUse();
          return;
        }
      } else {
        cmde.error("nousermentioned", "tagornameuser");
        return;
      }
    }

    try {
      final List<Warn> warns = mentionedMember.getWarns();
      StringBuilder result = new StringBuilder("empty");
      if (warns.size() != 0) {
        result = new StringBuilder();
        for (final Warn warn : warns) {
          result.append("\n`- ").append(BotInformation.DATE_PATTERN.format(new Date(warn.date())))
              .append(": `<@").append(warn.issuer()).append(">` > Reason: ")
              .append(
                  Optional.ofNullable(warn.reason()).map(str -> str.replace("`", "")).orElse(null))
              .append(" (ID: ").append(warn.date()).append(")`");
        }
      }
      cmde.success("warns", result.toString());
    } catch (final Exception ex) {
      ex.printStackTrace();
    }
  }
}
