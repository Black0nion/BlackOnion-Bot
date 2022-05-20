package com.github.black0nion.blackonionbot.api.impl.get;

import com.github.black0nion.blackonionbot.api.routes.IGetRoute;
import com.github.black0nion.blackonionbot.api.sessions.RestSession;
import com.github.black0nion.blackonionbot.bot.SlashCommandBase;
import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import com.github.black0nion.blackonionbot.stats.StatisticsManager;
import com.github.black0nion.blackonionbot.utils.config.Config;
import io.javalin.http.Context;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.util.Map;

import static com.github.black0nion.blackonionbot.bot.BotInformation.*;

public class Stats implements IGetRoute {

	@Override
	public Object handle(Context ctx, JSONObject body, Map<String, String> headers, @Nullable RestSession session, DiscordUser user) throws Exception {
		return new JSONObject()
			.put("commands", SlashCommandBase.getCommandCount())
			.put("code_stats", new JSONObject()
				.put("line_count", Config.getInstance().getMetadata().lines_of_code())
				.put("file_count", Config.getInstance().getMetadata().files()))
			.put("messages_sent", StatisticsManager.TOTAL_MESSAGES_SENT.get())
			.put("commands_executed", StatisticsManager.TOTAL_COMMANDS_EXECUTED.get())
			.put("cpu", new JSONObject()
				.put("cpu_name", CPU_NAME)
				.put("cpu_cores", OS_BEAN.getAvailableProcessors())
				.put("cpu_speed", CPU_MHZ))
			.put("guild_count", StatisticsManager.getGuildCount())
			.put("user_count", StatisticsManager.getUserCount())
			.put("prefix", Config.getInstance().getPrefix())
			.put("os", OS_NAME)
			.put("uptime", System.currentTimeMillis() - StatisticsManager.STARTUP_TIME)
			.put("version", Config.getInstance().getMetadata().version())
			.put("run_mode", Config.getInstance().getRunMode().name().toUpperCase())
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
