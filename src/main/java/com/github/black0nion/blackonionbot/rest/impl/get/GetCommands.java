package com.github.black0nion.blackonionbot.rest.impl.get;

import com.github.black0nion.blackonionbot.bot.SlashCommandBase;
import com.github.black0nion.blackonionbot.oauth.OAuthUser;
import com.github.black0nion.blackonionbot.rest.api.IGetRoute;
import com.github.black0nion.blackonionbot.rest.sessions.RestSession;
import io.javalin.http.Context;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Returns the registered slash commands in a JSON format, ready to be displayed by the website
 * <br>
 * Example JSON:
 * <code>[{"permissions":[],"subcommand_groups":[],"name":"weather","options":[{"name":"city_name","description":"The city to get weather information for.","type":"STRING","required":true}],"description":"Used to get weather information for a city.","subcommands":{}}]</code>
 */
public class GetCommands implements IGetRoute {

	private final SlashCommandBase slashCommandBase;

	public GetCommands(SlashCommandBase slashCommandBase) {
		this.slashCommandBase = slashCommandBase;
	}

	@Override
	public Object handle(Context ctx, JSONObject body, @Nullable RestSession session, OAuthUser user) throws Exception {
		return slashCommandBase.getCommandsJson();
	}

	@Nonnull
	@Override
	public String url() {
		return "commands";
	}
}