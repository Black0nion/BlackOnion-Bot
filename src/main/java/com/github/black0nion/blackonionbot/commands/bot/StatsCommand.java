package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.bot.SlashCommandBase;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.commands.TextCommand;
import com.github.black0nion.blackonionbot.stats.StatisticsManager;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.utils.config.Config;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.Instant;

import static com.github.black0nion.blackonionbot.bot.BotInformation.*;
import static com.github.black0nion.blackonionbot.utils.config.Config.metadata;

public class StatsCommand extends TextCommand {

	public StatsCommand() {
		this.setCommand("stats", "botstats");
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		try {
			final long diff = System.currentTimeMillis() - StatisticsManager.STARTUP_TIME;

			cmde.reply(cmde
				.success()
				.setTitle("Bot Stats")
				.addField("prefix", "`" + guild.getPrefix() + "`", true)
				.addField("runmode", Config.run_mode.name().toUpperCase(), true)
				.addField("os", OS_NAME, true)
				.addField("cpuname", CPU_NAME, true)
				.addField("cpucores", OSBEAN.getAvailableProcessors(), true)
				.addField("cpuspeed", CPU_MHZ, true)
				.addField("lines", metadata.lines_of_code(), true)
				.addField("files", metadata.files(), true)
				.addField("commandsexecuted", StatisticsManager.COMMANDS_EXECUTED.get(), true)
				.addField("messagessent", StatisticsManager.MESSAGES_SENT.get(), true)
				.addField("commands", CommandBase.commandsArray.size() + SlashCommandBase.commands.size(), true)
				.addField("ping", e.getJDA().getGatewayPing() + "ms", true)
				.addField("usercount", StatisticsManager.getUserCount(), true)
				.addField("guildcount", StatisticsManager.getGuildCount(), true)
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