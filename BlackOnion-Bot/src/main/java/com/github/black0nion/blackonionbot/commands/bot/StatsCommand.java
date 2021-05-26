package com.github.black0nion.blackonionbot.commands.bot;

import static com.github.black0nion.blackonionbot.bot.BotInformation.cpuMhz;
import static com.github.black0nion.blackonionbot.bot.BotInformation.cpuName;
import static com.github.black0nion.blackonionbot.bot.BotInformation.osBean;
import static com.github.black0nion.blackonionbot.bot.BotInformation.osName;

import java.time.Instant;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.ValueManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class StatsCommand extends Command {
	
	public StatsCommand() {
		this.setCommand("stats", "botstats");
	}

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		try {
	        long diff = System.currentTimeMillis() - Bot.startTime;
			long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);
			
			EmbedBuilder builder = cmde.success()
				.setTitle("Bot Stats")
				.setFooter(author.getName() + author.getDiscriminator(), author.getEffectiveAvatarUrl())
				.addField("Prefix", "``" + guild.getPrefix() + "``", true)
				.addField("RunMode", Bot.runMode.name().toUpperCase(), true)
				.addField("OS", osName, true)
				.addField("CPU Name", cpuName, true)
				.addField("CPU Cores", String.valueOf(osBean.getAvailableProcessors()), true)
				.addField("CPU Speed", cpuMhz, true)
				.addField("Lines", String.valueOf(BotInformation.line_count), true)
				.addField("Files", String.valueOf(BotInformation.file_count), true)
				.addField("Commands executed", String.valueOf(ValueManager.getInt("commandsExecuted")), true)
				.addField("Messages sent", String.valueOf(ValueManager.getInt("messagesSent")), true)
				.addField("Commands", String.valueOf(CommandBase.commandsArray.size()), true)
				.addField("Ping", e.getJDA().getGatewayPing() + "ms", true)
				.addField("Uptime", (diffDays != 0 ? diffDays + " days" : "") + (diffHours != 0 ? " " + diffHours + " hours" : "") + (diffMinutes != 0 ? " " + diffMinutes + " minutes" : "") + (diffSeconds != 0 ? " " + diffSeconds + " seconds" : ""), false)
				.setThumbnail("https://image.sv-studios.net/15d06c22eb6b0b8dfbdeda94a56c878d15.png")
				.setTimestamp(Instant.now());
			cmde.reply(builder);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}