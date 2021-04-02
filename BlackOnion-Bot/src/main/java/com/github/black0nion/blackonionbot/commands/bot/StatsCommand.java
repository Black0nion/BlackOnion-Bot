package com.github.black0nion.blackonionbot.commands.bot;

import static com.github.black0nion.blackonionbot.bot.BotInformation.cpuMhz;
import static com.github.black0nion.blackonionbot.bot.BotInformation.cpuName;
import static com.github.black0nion.blackonionbot.bot.BotInformation.osBean;
import static com.github.black0nion.blackonionbot.bot.BotInformation.osName;

import java.time.Instant;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.enums.Category;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.ValueManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class StatsCommand implements Command {

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		try {
	        long diff = System.currentTimeMillis() - Bot.startTime;
			long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);
			
			EmbedBuilder builder = EmbedUtils.getSuccessEmbed(author, guild)
				.setTitle("Bot Stats")
				.setFooter(author.getName() + author.getDiscriminator(), author.getEffectiveAvatarUrl())
				.addField("Prefix", "``" + BotInformation.getPrefix(guild) + "``", true)
				.addField("RunMode", Bot.runMode.name().toUpperCase(), true)
				.addField("OS", osName, true)
				.addField("CPU Name", cpuName, true)
				.addField("CPU Cores", String.valueOf(osBean.getAvailableProcessors()), true)
				.addField("CPU Speed", cpuMhz, true)
				.addField("Lines", String.valueOf(BotInformation.line_count), true)
				.addField("Files", String.valueOf(BotInformation.file_count), true)
				.addField("Commands executed", String.valueOf(ValueManager.getInt("commandsExecuted")), true)
				.addField("Messages sent", String.valueOf(ValueManager.getInt("messagesSent")), true)
				.addField("Commands", String.valueOf(CommandBase.commands.size()), true)
				.addField("Ping", e.getJDA().getGatewayPing() + "ms", true)
				.addField("Uptime", (diffDays != 0 ? diffDays + " days" : "") + (diffHours != 0 ? " " + diffHours + " hours" : "") + (diffMinutes != 0 ? " " + diffMinutes + " minutes" : "") + (diffSeconds != 0 ? " " + diffSeconds + " seconds" : ""), false)
				.setThumbnail("https://image.sv-studios.net/15d06c22eb6b0b8dfbdeda94a56c878d15.png")
				.setTimestamp(Instant.now());
			channel.sendMessage(builder.build()).queue();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public Category getCategory() {
		return Category.BOT;
	}
	
	@Override
	public String[] getCommand() {
		return new String[] {"stats"};
	}
}
