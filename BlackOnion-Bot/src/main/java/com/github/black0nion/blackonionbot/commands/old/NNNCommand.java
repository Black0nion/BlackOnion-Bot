package com.github.black0nion.blackonionbot.commands.old;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.enums.CommandVisibility;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class NNNCommand implements Command {

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		try {
		final String gameDate = "2020-12-01T00:00:00Z";
		final SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.GERMANY);
		apiFormat.setTimeZone(TimeZone.getTimeZone("MEZ"));
		final Date dateOfGame = apiFormat.parse(gameDate);
		final long millis = dateOfGame.getTime() - System.currentTimeMillis();
		ZoneId z = ZoneId.of( "Europe/Berlin" ) ;
		LocalDate today = LocalDate.now(z) ;
		LocalDate start = LocalDate.of(2020, 11, 1);
		LocalDate stop = LocalDate.of(2020, 11, 30);

		long totalDays = ChronoUnit.DAYS.between( start , stop ) ;
		long elapsedDays = ChronoUnit.DAYS.between( start , today ) ;

		long percentComplete = ( elapsedDays * 100 ) / totalDays ;
		
		final String hms = String.format("%02d Tage, %02d Stunden und %02d Minuten", TimeUnit.MILLISECONDS.toDays(millis),
						TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis)),
						TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)));
		channel.sendMessage("Zeit bis zum Ende des NoNutNovembers: " + hms + ", bereits geschafft sind " + percentComplete + "% der gesamten Zeit!").queue();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public CommandVisibility getVisisbility() {
		return CommandVisibility.HIDDEN;
	}

	@Override
	public String[] getCommand() {
		return new String[] {"nnn", "nonutnovember"};
	}
	
}
