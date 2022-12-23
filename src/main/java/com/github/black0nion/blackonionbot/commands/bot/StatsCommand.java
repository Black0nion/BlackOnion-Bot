package com.github.black0nion.blackonionbot.commands.bot;

import com.github.black0nion.blackonionbot.bot.SlashCommandBase;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.immutable.ConfigFileLoader;
import com.github.black0nion.blackonionbot.config.immutable.api.Config;
import com.github.black0nion.blackonionbot.stats.StatisticsManager;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;

import static com.github.black0nion.blackonionbot.bot.BotInformation.*;

public class StatsCommand extends SlashCommand {

	private final SlashCommandBase slashCommandBase;

	public StatsCommand(Config config, SlashCommandBase slashCommandBase) {
		super("stats", "Shows statistics regarding the bot.", config);
		this.slashCommandBase = slashCommandBase;
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		final long diff = System.currentTimeMillis() - StatisticsManager.STARTUP_TIME;

		cmde.reply(cmde
			.success()
			.setTitle("Bot Stats")
			.addField("prefix", "`" + "/" + "`", true)
			.addField("runmode", config.getRunMode().name().toUpperCase(), true)
			.addField("os", OS_NAME, true)
			.addField("cpuname", CPU_NAME, true)
			.addField("cpucores", OS_BEAN.getAvailableProcessors(), true)
			.addField("cpuspeed", CPU_MHZ, true)
			.addField("lines", ConfigFileLoader.getMetadata().lines_of_code(), true)
			.addField("files", ConfigFileLoader.getMetadata().files(), true)
			.addField("commandsexecuted", (int) StatisticsManager.TOTAL_COMMANDS_EXECUTED.get(), true)
			.addField("messagessent", (int) StatisticsManager.TOTAL_MESSAGES_SENT.get(), true)
			.addField("commands", slashCommandBase.getCommandCount(), true)
			.addField("ping", StatisticsManager.getGatewayPing() + "ms", true)
			.addField("usercount", StatisticsManager.getUserCount(), true)
			.addField("guildcount", StatisticsManager.getGuildCount(), true)
			.addField("uptime", Utils.formatDuration(Duration.ofMillis(diff)), true)
			.addField("version", ConfigFileLoader.getMetadata().version(), true)
			.setThumbnail(e.getJDA().getSelfUser().getAvatarUrl() + "?size=512")
			.setTimestamp(Instant.now())
		);
	}
}
