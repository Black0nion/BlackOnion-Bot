package com.github.black0nion.blackonionbot.commands.fun;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.enums.Category;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class GiveawayCommand implements Command {
	
	ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
	
	Random random = new Random();

	@Override
	public String[] getCommand() {
		return new String[] { "giveaway" };
	}

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		int duration = -1;
		int winnersCount = -1;
		Date endDate = null;
		try {
			winnersCount = Integer.parseInt(args[2]);
		} catch (Exception ignored) {}
		
		try { endDate = format.parse(args[1]); } catch (Exception ignored) {
			try {
				duration = Integer.parseInt(args[1]);
				Calendar cal = Calendar.getInstance(); 
				cal.setTimeInMillis(cal.getTimeInMillis() + (long) duration);
				endDate = cal.getTime();
			} catch (Exception ignored2) {}
		}
		
		if (duration == -1 || endDate == null || winnersCount == -1) {
			channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("wrongargument", LanguageSystem.getTranslatedString("pleaseuse", author, guild) + " " + getSyntax(), false).build()).queue();
			return;
		}
		
		if (winnersCount > 100) {
			channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("toomanywinners", "maxhundretwinners", false).build()).queue();
			return;
		}
		
		if (TimeUnit.SECONDS.convert(new Date().getTime() - endDate.getTime(), TimeUnit.MILLISECONDS) < 60) {
			channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("tooshort", "minonemin", false).build()).queue();
			return;
		}
		
		Message msg = channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild).setTitle("GIVEAWAY").addField(LanguageSystem.getTranslatedString("giveawayfor", author, guild).replace("%item%", String.join(" ", Utils.subArray(args, 3, args.length - 1))).replace("%winners%", String.valueOf(winnersCount)), LanguageSystem.getTranslatedString("giveawayend", author, guild).replace("%end%", format.format(endDate)), false).build()).submit().join();
		msg.addReaction("U+1F389").queue();
		final int winnas = winnersCount;
		executor.schedule(() -> {
			try {
				final List<User> users = msg.retrieveReactionUsers("\uD83C\uDF89").submit().join();
				if (users.size() == 0 || users.stream().filter(user -> {return user.getIdLong() != e.getJDA().getSelfUser().getIdLong();}).count() == 0) {
					msg.editMessage(EmbedUtils.getSuccessEmbed(author, guild).setTitle("GIVEAWAY").addField("nowinner", "nobodyparticipated", false).build()).queue();
					return;
				}
				users.remove(e.getJDA().getSelfUser());
				String[] winners = new String[winnas < users.size() ? winnas : users.size()];
				
				Collections.shuffle(users, random);
				
				for (int i = 0; i < winners.length; i++) {
					winners[i] = users.get(i).getAsMention();
				}
				
				msg.editMessage(EmbedUtils.getSuccessEmbed(author, guild).setTitle("GIVEAWAY").addField("Winner Winner Chicken Dinner :)", LanguageSystem.getTranslatedString("giveawaywinner", author, guild).replace("%winner%", String.join("\n", winners)), false).build()).queue();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}, endDate.getTime() - Calendar.getInstance().getTime().getTime(), TimeUnit.MILLISECONDS);
	}
	
	@Override
	public Category getCategory() {
		return Category.FUN;
	}
	
	@Override
	public int getRequiredArgumentCount() {
		return 3;
	}
	
	@Override
	public Permission[] getRequiredPermissions() {
		return new Permission[] { Permission.MANAGE_CHANNEL };
	}

	@Override
	public String getSyntax() {
		return "<time in ms | date in " + format.toPattern() + "> <winners> <item to give away>";
	}
}
