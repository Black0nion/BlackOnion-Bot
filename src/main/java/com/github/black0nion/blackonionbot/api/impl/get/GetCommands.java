package com.github.black0nion.blackonionbot.api.impl.get;

import com.github.black0nion.blackonionbot.api.BlackSession;
import com.github.black0nion.blackonionbot.api.routes.IGetRoute;
import com.github.black0nion.blackonionbot.bot.SlashCommandBase;
import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import io.javalin.http.Context;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * Returns the registered slash commands in a JSON format, ready to be displayed by the website
 *
 * Example JSON:
 * <code>[{"permissions":[],"subcommand_groups":[],"name":"weather","options":[{"name":"city_name","description":"The city to get weather information for.","type":"STRING","required":true}],"description":"Used to get weather information for a city.","subcommands":{}}]</code>
 */
public class GetCommands implements IGetRoute {
	@Override
	public Object handle(Context ctx, JSONObject body, Map<String, String> headers, @Nullable BlackSession session, DiscordUser user) throws Exception {
		return SlashCommandBase.getCommandsJson();
	}

	@Nonnull
	@Override
	public String url() {
		return "commands";
	}
}