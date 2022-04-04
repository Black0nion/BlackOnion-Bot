package com.github.black0nion.blackonionbot.api.impl.get;

import com.github.black0nion.blackonionbot.api.BlackSession;
import com.github.black0nion.blackonionbot.api.routes.IGetRoute;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.bot.SlashCommandBase;
import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import com.github.black0nion.blackonionbot.stats.StatisticsManager;
import com.github.black0nion.blackonionbot.utils.config.Config;
import io.javalin.http.Context;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;

import static com.github.black0nion.blackonionbot.bot.BotInformation.*;
import static com.github.black0nion.blackonionbot.utils.config.Config.metadata;

public class Stats implements IGetRoute {

	@Override
	public Object handle(Context ctx, JSONObject body, Map<String, String> headers, @Nullable BlackSession session, DiscordUser user) throws Exception {
		return new JSONObject()
			.put("commands", CommandBase.commandsArray.size() + SlashCommandBase.commands.size())
			.put("code_stats", new JSONObject()
					.put("line_count", Config.metadata.lines_of_code())
					.put("file_count", Config.metadata.files()))
			.put("messages_sent", StatisticsManager.TOTAL_MESSAGES_SENT.get())
			.put("commands_executed", StatisticsManager.TOTAL_COMMANDS_EXECUTED.get())
			.put("cpu", new JSONObject()
					.put("cpu_name", CPU_NAME)
					.put("cpu_cores", OSBEAN.getAvailableProcessors())
					.put("cpu_speed", CPU_MHZ))
			.put("guild_count", StatisticsManager.getGuildCount())
			.put("user_count", StatisticsManager.getUserCount())
			.put("prefix", Config.prefix)
			.put("os", OS_NAME)
			.put("uptime", System.currentTimeMillis() - StatisticsManager.STARTUP_TIME)
			.put("version", metadata.version())
			.put("run_mode", Config.run_mode.name().toUpperCase())
			.put("ping", StatisticsManager.getGatewayPing());
	}

	@Override
	public @Nonnull String url() {
		return "stats";
	}

	@Override
	public boolean isJson() {
		return true;
	}
}