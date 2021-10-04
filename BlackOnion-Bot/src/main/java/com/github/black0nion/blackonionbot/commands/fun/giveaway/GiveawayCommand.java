package com.github.black0nion.blackonionbot.commands.fun.giveaway;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.systems.giveaways.GiveawaySystem;
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class GiveawayCommand extends SlashCommand {

	public GiveawayCommand() {
	    this.setData(new CommandData("giveaway", "Create a giveaway")
		    .addOption(OptionType.STRING, "date", "The time in s or date in " + format.toPattern(), true)
		    .addOption(OptionType.INTEGER, "winnercount", "The amount of winners", true)
		    .addOption(OptionType.STRING, "item", "The item to give away", true))
	    .setRequiredPermissions(Permission.MESSAGE_MANAGE);
	}

	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm");

	@Override
	public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	    long duration = -1;
	    long winnersCount = -1;
	    Date endDate = null;
	    winnersCount = e.getOptionsByType(OptionType.INTEGER).get(0).getAsLong();

	    try {
		endDate = format.parse(e.getOption("date").getAsString());
	    } catch (final Exception ignored) {
		try {
		    duration = e.getOption("date").getAsLong();
		    duration *= 1000;
		    final Calendar cal = Calendar.getInstance();
		    cal.setTimeInMillis(cal.getTimeInMillis() + duration);
		    endDate = cal.getTime();
		} catch (final Exception ignored2) {
		    cmde.sendPleaseUsePrivate();
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

	    if (TimeUnit.SECONDS.convert(endDate.getTime() - new Date().getTime(), TimeUnit.MILLISECONDS) < 60) {
		cmde.error("tooshort", "minonemin");
		return;
	    }

	    final String item = e.getOption("item").getAsString();
	    final Date finalEndDate = endDate;
	    final int finalWinnersCount = (int) winnersCount;

	    final EmbedBuilder giveawayMessage = cmde.success().setTitle(cmde.getTranslation("giveawayfor", new Placeholder("item", item))).setDescription(cmde.getTranslation("giveawaydesc", new Placeholder("item", item), new Placeholder("winners", String.valueOf(winnersCount)), new Placeholder("end", format.format(endDate).replace("_", " ")), new Placeholder("user", author.getAsMention())));
	    e.reply(cmde.getTranslation("giveawaycreated")).setEphemeral(true).queue();
	    e.getChannel().sendMessageEmbeds(giveawayMessage.build()).queue(msg -> {
		msg.addReaction("U+1F389").queue();
		msg.editMessageEmbeds(giveawayMessage.setFooter(cmde.getTranslation("giveawayid", new Placeholder("id", msg.getId()))).build()).queue();
		GiveawaySystem.createGiveaway(finalEndDate, msg.getIdLong(), channel.getIdLong(), author.getIdLong(), guild.getIdLong(), item, finalWinnersCount);
	    });
	}
}