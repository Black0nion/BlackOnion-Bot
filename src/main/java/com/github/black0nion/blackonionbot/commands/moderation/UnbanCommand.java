package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.misc.CustomPermission;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.black0nion.blackonionbot.commands.TextCommand;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UnbanCommand extends SlashCommand {
  private static final String USER = "user";
  private static final String REASON = "reason";

  public UnbanCommand() {
    super(builder(Commands.slash("unban", "Used to unban a user")
        .addOption(OptionType.USER, USER, "The user to unban")
        .addOption(OptionType.STRING, REASON, "The reason for the unban"))
            .setRequiredPermissions(Permission.BAN_MEMBERS)
            .setRequiredBotPermissions(Permission.BAN_MEMBERS));
  }

  @Override
  public void execute(SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e,
      BlackMember member, BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
    var user = e.getOption(USER, OptionMapping::getAsUser);
    var reason = e.getOption(REASON, OptionMapping::getAsString);
    guild.retrieveBan(user).queue(success -> {
      guild.unban(user).reason(reason).queue(
          v -> e.reply("I have unbanned the user " + user.getAsMention() + " for " + reason)
              .queue(),
          fail -> e.reply("I could not unban the user " + user.getAsMention() + " for " + reason)
              .queue());

    }, fail -> e.reply("I could not find the user " + user.getAsMention() + " to unban").queue());
  }
}
