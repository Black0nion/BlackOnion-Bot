package com.github.black0nion.blackonionbot.commands.bot;

import static com.github.black0nion.blackonionbot.bot.BotInformation.CPU_MHZ;
import static com.github.black0nion.blackonionbot.bot.BotInformation.CPU_NAME;
import static com.github.black0nion.blackonionbot.bot.BotInformation.OSBEAN;
import static com.github.black0nion.blackonionbot.bot.BotInformation.OS_NAME;

import java.time.Instant;
import java.util.stream.Collectors;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import net.dv8tion.jda.api.entities.Message;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.utils.ValueManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class StatsCommand extends Command {

    public StatsCommand() {
	this.setCommand("stats", "botstats");
    }

    @Override
    public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	try {
	    final long diff = System.currentTimeMillis() - Bot.startTime;

	    final EmbedBuilder builder = cmde
		    .success()
		    .setTitle("Bot Stats")
		    .setFooter(author.getName() + author.getDiscriminator(), author.getEffectiveAvatarUrl())
		    .addField("prefix", "``" + guild.getPrefix() + "``", true)
		    .addField("runmode", Bot.runMode.name().toUpperCase(), true)
		    .addField("os", OS_NAME, true)
		    .addField("cpuname", CPU_NAME, true)
		    .addField("cpucores", String.valueOf(OSBEAN.getAvailableProcessors()), true)
		    .addField("cpuspeed", CPU_MHZ, true)
		    .addField("lines", String.valueOf(BotInformation.LINE_COUNT), true)
		    .addField("files", String.valueOf(BotInformation.FILE_COUNT), true)
		    .addField("commandsexecuted", String.valueOf(ValueManager.getInt("commandsExecuted")), true)
		    .addField("messagessent", String.valueOf(ValueManager.getInt("messagesSent")), true)
		    .addField("commands", String.valueOf(CommandBase.commandsArray.size()), true)
		    .addField("ping", e.getJDA().getGatewayPing() + "ms", true)
		    .addField("usercount", String.valueOf(e.getJDA().getGuilds().stream().map(Guild::getMemberCount).collect(Collectors.summingInt(Integer::intValue))), true)
		    .addField("guildcount", String.valueOf(e.getJDA().getGuildCache().size()), true)
		    .addField("uptime", Utils.parseDate(diff), true)
		    .addField("version", BotInformation.BOT_VERSION, true)
		    .setThumbnail("https://image.sv-studios.net/15d06c22eb6b0b8dfbdeda94a56c878d15.png")
		    .setTimestamp(Instant.now());
	    cmde.reply(builder);
	} catch (final Exception ex) {
	    ex.printStackTrace();
	}
    }
}