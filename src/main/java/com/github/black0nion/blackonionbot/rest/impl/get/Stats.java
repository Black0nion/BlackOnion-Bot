package com.github.black0nion.blackonionbot.rest.impl.get;

import com.github.black0nion.blackonionbot.bot.SlashCommandBase;
import com.github.black0nion.blackonionbot.config.ConfigFileLoader;
import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import com.github.black0nion.blackonionbot.rest.api.IGetRoute;
import com.github.black0nion.blackonionbot.rest.sessions.RestSession;
import com.github.black0nion.blackonionbot.stats.StatisticsManager;
import com.github.black0nion.blackonionbot.config.api.Config;
import io.javalin.http.Context;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import javax.annotation.Nonnull;

import static com.github.black0nion.blackonionbot.bot.BotInformation.*;

public class Stats implements IGetRoute {

	private final Config config;

	public Stats(Config config) {
		this.config = config;
	}

	@Override
	public Object handle(Context ctx, JSONObject body, @Nullable RestSession session, DiscordUser user) throws Exception {
		return new JSONObject()
			.put("commands", SlashCommandBase.getCommandCount())
			.put("code_stats", new JSONObject()
				.put("line_count", ConfigFileLoader.getMetadata().lines_of_code())
				.put("file_count", ConfigFileLoader.getMetadata().files()))
			.put("messages_sent", StatisticsManager.TOTAL_MESSAGES_SENT.get())
			.put("commands_executed", StatisticsManager.TOTAL_COMMANDS_EXECUTED.get())
			.put("cpu", new JSONObject()
				.put("cpu_name", CPU_NAME)
				.put("cpu_cores", OS_BEAN.getAvailableProcessors())
				.put("cpu_speed", CPU_MHZ))
			.put("guild_count", StatisticsManager.getGuildCount())
			.put("user_count", StatisticsManager.getUserCount())
			.put("prefix", "/")
			.put("os", OS_NAME)
			.put("uptime", System.currentTimeMillis() - StatisticsManager.STARTUP_TIME)
			.put("version", ConfigFileLoader.getMetadata().version())
			.put("run_mode", config.getRunMode().name().toUpperCase())
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
