package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class ClearUntilCommand extends SlashCommand {

  public ClearUntilCommand() {
    super(builder(Commands.slash("clearuntil", "Clear all messages until a certain one").addOption(
        OptionType.STRING, "messageid", "The message id of the last message to be kept", true))
            .setRequiredBotPermissions(Permission.MESSAGE_MANAGE)
            .setRequiredPermissions(Permission.MESSAGE_MANAGE).setEphemeral());
  }

  @Override
  public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member,
      BlackUser author, BlackGuild guild, TextChannel channel) {
    try {
      String messageIdString = e.getOption("messageid", OptionMapping::getAsString);
      if (!Utils.isLong(messageIdString)) {
        cmde.sendPleaseUse();
        return;
      }
      final long messageId = Long.parseLong(messageIdString);
      channel.retrieveMessageById(messageId).queue(msg -> {
        try {
          channel.getHistoryAfter(msg, 101).queue(msgs -> {
            final int msgsize = msgs.size();
            if (msgsize == 0 || msgsize > 100) {
              cmde.error("toomanymessages", "toomanymessagesinfo",
                  new Placeholder("msgcount", msgsize));
              return;
            }
            final OffsetDateTime lastValidTime =
                OffsetDateTime.now(ZoneOffset.UTC).minusWeeks(2).plusSeconds(1);
            final List<Message> messages = new ArrayList<>();
            int i = msgsize + 1;
            for (final Message m : msgs.getRetrievedHistory()) {
              if (!m.isPinned() && m.getTimeCreated().isAfter(lastValidTime)) {
                messages.add(m);
              }
              if (--i <= 0) {
                break;
              }
            }

            ClearCommand.deleteMessages(cmde, channel, messages.size(), messages);
          });
        } catch (final Exception ex) {
          if (!(ex instanceof IllegalArgumentException)) {
            ex.printStackTrace();
            cmde.exception(ex);
          } else {
            ex.printStackTrace();
            cmde.send("messagestooold");
          }
        }
      }, err -> cmde.error("nomessagesfound", "pleaseinputmessage"));
    } catch (final Exception ignored) {
      cmde.sendPleaseUse();
    }
  }
}
