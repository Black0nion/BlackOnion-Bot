package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.commands.TextCommand;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.misc.CustomPermission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SetBotLogsChannelCommand extends TextCommand {

  public SetBotLogsChannelCommand() {
    this.setCommand("setbotlogschannel").setRequiredCustomPermissions(CustomPermission.ADMIN);
  }

  @Override
  public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e,
      final Message message, final BlackMember member, final BlackUser author,
      final BlackGuild guild, final TextChannel channel) {
    BotInformation.botLogsChannel = channel.getIdLong();
    guild.save("botlogschannel", channel.getIdLong());
    cmde.success("savedbotlogschannel", "thisisbotlogschannel");
  }
}
