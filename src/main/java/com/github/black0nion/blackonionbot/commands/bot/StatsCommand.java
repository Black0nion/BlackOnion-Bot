package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.logging.StatisticsManager;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.utils.config.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.time.Instant;

import static com.github.black0nion.blackonionbot.bot.BotInformation.*;
import static com.github.black0nion.blackonionbot.utils.config.Config.metadata;

public class StatsCommand extends Command {

	public StatsCommand() {
		this.setCommand("stats", "botstats");
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		try {
			final long diff = System.currentTimeMillis() - Bot.startTime;

			cmde.reply(cmde
				.success()
				.setTitle("Bot Stats")
				.setFooter(author.getName() + author.getDiscriminator(), author.getEffectiveAvatarUrl())
				.addField("prefix", "`" + guild.getPrefix() + "`", true)
				.addField("runmode", Bot.runMode.name().toUpperCase(), true)
				.addField("os", OS_NAME, true)
				.addField("cpuname", CPU_NAME, true)
				.addField("cpucores", String.valueOf(OSBEAN.getAvailableProcessors()), true)
				.addField("cpuspeed", CPU_MHZ, true)
				.addField("lines", String.valueOf(metadata.lines_of_code()), true)
				.addField("files", String.valueOf(metadata.files()), true)
				.addField("commandsexecuted", String.valueOf(StatisticsManager.getTotalCommands()), true)
				.addField("messagessent", String.valueOf(StatisticsManager.getMessagesSent()), true)
				.addField("commands", String.valueOf(CommandBase.commandsArray.size()), true)
				.addField("ping", e.getJDA().getGatewayPing() + "ms", true)
				.addField("usercount", String.valueOf((Integer) e.getJDA().getGuilds().stream().map(Guild::getMemberCount).mapToInt(Integer::intValue).sum()), true)
				.addField("guildcount", String.valueOf(e.getJDA().getGuildCache().size()), true)
				.addField("uptime", Utils.parseDate(diff), true)
				.addField("version", Config.metadata.version(), true)
				.setThumbnail(e.getJDA().getSelfUser().getAvatarUrl() + "?size=512")
				.setTimestamp(Instant.now())
			);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}
}