package com.github.black0nion.blackonionbot.commands.fun;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.giveaways.GiveawaysSystem;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class GiveawayCommand extends Command {
	
	public GiveawayCommand() {
		this.setCommand("giveaway")
			.setSyntax("<time in s | date in " + format.toPattern() + "> <winners> <item to give away>")
			.setRequiredArgumentCount(3)
			.setRequiredPermissions(Permission.MANAGE_CHANNEL);
	}
	
	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm");

	@Override
	public String[] getCommand() {
		return new String[] { "giveaway" };
	}

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		int duration = -1;
		int winnersCount = -1;
		Date endDate = null;
		try {
			winnersCount = Integer.parseInt(args[2]);
		} catch (Exception ignored) {}
		
		try { endDate = format.parse(args[1]); } catch (Exception ignored) {
			try {
				duration = Integer.parseInt(args[1]);
				duration *= 1000;
				Calendar cal = Calendar.getInstance(); 
				cal.setTimeInMillis(cal.getTimeInMillis() + (long) duration);
				endDate = cal.getTime();
			} catch (Exception ignored2) {}
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
		
		final String item = String.join(" ", Utils.subArray(args, 3, args.length - 1));
		final Date finalEndDate = endDate;
		final int finalWinnersCount = winnersCount;
		
		cmde.reply(cmde.success().setTitle(cmde.getTranslation("giveawayfor", new Placeholder("item", item))).setDescription(cmde.getTranslation("giveawaydesc", new Placeholder("item", item), new Placeholder("winners", String.valueOf(winnersCount)), new Placeholder("end", format.format(endDate).replace("_", " ")))), msg -> {
			msg.addReaction("U+1F389").queue();
			GiveawaysSystem.createGiveaway(finalEndDate, msg.getIdLong(), channel.getIdLong(), guild.getIdLong(), item, finalWinnersCount);
		});
	}
}