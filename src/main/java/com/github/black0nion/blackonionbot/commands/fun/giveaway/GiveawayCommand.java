package com.github.black0nion.blackonionbot.commands.fun.giveaway;

import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.commands.TextCommand;
import com.github.black0nion.blackonionbot.systems.giveaways.GiveawaySystem;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class GiveawayCommand extends TextCommand {

  public GiveawayCommand() {
    this.setCommand("giveaway")
        .setSyntax("<time in s | date in " + format.toPattern() + "> <winners> <item to give away>")
        .setRequiredArgumentCount(3).setRequiredPermissions(Permission.MANAGE_CHANNEL);
  }

  private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm");

  @Override
  public String[] getCommand() {
    return new String[] {"giveaway"};
  }

  @Override
  public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e,
      final Message message, final BlackMember member, final BlackUser author,
      final BlackGuild guild, final TextChannel channel) {
    int duration = -1;
    int winnersCount = -1;
    Date endDate = null;
    try {
      winnersCount = Integer.parseInt(args[2]);
    } catch (final Exception ignored) {
    }

    try {
      endDate = format.parse(args[1]);
    } catch (final Exception ignored) {
      try {
        duration = Integer.parseInt(args[1]);
        duration *= 1000;
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(cal.getTimeInMillis() + duration);
        endDate = cal.getTime();
      } catch (final Exception ignored2) {
      }
    }

    if (duration == -1 || endDate == null || winnersCount == -1) {
      cmde.sendPleaseUse();
      return;
    }

    if (winnersCount > 100) {
      cmde.error("toomanywinners", "maxhundretwinners");
      return;
    }

    if (TimeUnit.SECONDS.convert(endDate.getTime() - new Date().getTime(),
        TimeUnit.MILLISECONDS) < 60) {
      cmde.error("tooshort", "minonemin");
      return;
    }

    final String item = String.join(" ", Utils.subArray(args, 3, args.length - 1));
    final Date finalEndDate = endDate;
    final int finalWinnersCount = winnersCount;

    final EmbedBuilder giveawayMessage =
        cmde.success().setTitle(cmde.getTranslation("giveawayfor", new Placeholder("item", item)))
            .setDescription(cmde.getTranslation("giveawaydesc", new Placeholder("item", item),
                new Placeholder("winners", String.valueOf(winnersCount)),
                new Placeholder("end", format.format(endDate).replace("_", " ")),
                new Placeholder("user", author.getAsMention())));
    cmde.reply(giveawayMessage, msg -> {
      msg.addReaction("U+1F389").queue();
      msg.editMessageEmbeds(giveawayMessage
          .setFooter(cmde.getTranslation("giveawayid", new Placeholder("id", msg.getId()))).build())
          .queue();
      GiveawaySystem.createGiveaway(finalEndDate, msg.getIdLong(), channel.getIdLong(),
          author.getIdLong(), guild.getIdLong(), item, finalWinnersCount);
    });
  }
}
