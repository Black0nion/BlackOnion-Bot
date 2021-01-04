package com.github.ahitm_2020_2025.blackonionbot.commands.bot;

import static com.github.ahitm_2020_2025.blackonionbot.bot.BotInformation.cpuMhz;
import static com.github.ahitm_2020_2025.blackonionbot.bot.BotInformation.cpuName;
import static com.github.ahitm_2020_2025.blackonionbot.bot.BotInformation.os;
import static com.github.ahitm_2020_2025.blackonionbot.bot.BotInformation.osBean;
import static com.github.ahitm_2020_2025.blackonionbot.bot.BotInformation.osName;

import java.time.Instant;

import com.github.ahitm_2020_2025.blackonionbot.bot.Bot;
import com.github.ahitm_2020_2025.blackonionbot.bot.BotInformation;
import com.github.ahitm_2020_2025.blackonionbot.enums.Category;
import com.github.ahitm_2020_2025.blackonionbot.oldcommands.Command;
import com.github.ahitm_2020_2025.blackonionbot.utils.ValueManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class StatsCommand implements Command {

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, MessageChannel channel) {
		try {
	        
			EmbedBuilder builder = new EmbedBuilder()
				.setTitle("Bot Stats")
				.setFooter(author.getName() + author.getDiscriminator(), author.getEffectiveAvatarUrl())
				.addField("Prefix", BotInformation.prefix, true)
				.addField("RunMode", Bot.runMode.name().toUpperCase(), true)
				.addField("OS", osName, true)
				.addField("CPU Name", cpuName, true)
				.addField("CPU Cores", String.valueOf(osBean.getAvailableProcessors()), true)
				.addField("CPU Speed", cpuMhz, true)
				.addField("Lines", String.valueOf(BotInformation.line_count), true)
				.addField("Files", String.valueOf(BotInformation.file_count), true)
				.addField("Commands executed", String.valueOf(ValueManager.getInt("commandsExecuted")), true)
				.addField("Messages sent", String.valueOf(ValueManager.getInt("messagesSent")), true)
				.addField("Ping", e.getJDA().getGatewayPing() + "ms", true)
				.setThumbnail("https://avatars1.githubusercontent.com/u/14834294?s=400&v=4")
				.setTimestamp(Instant.now());
			channel.sendMessage(builder.build()).queue();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public String getDescription() {
		return "Zeigt Statistiken über den Bot an";
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
