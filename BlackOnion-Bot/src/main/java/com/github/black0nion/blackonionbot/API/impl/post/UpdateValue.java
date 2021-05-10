package com.github.black0nion.blackonionbot.API.impl.post;

import java.util.HashMap;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.API.PostRequest;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.systems.dashboard.Dashboard;
import com.github.black0nion.blackonionbot.systems.dashboard.values.DashboardValue;
import com.github.black0nion.blackonionbot.utils.DiscordUser;

import net.dv8tion.jda.api.entities.Guild;
import spark.Request;
import spark.Response;

public class UpdateValue implements PostRequest {

	@Override
	public String handle(Request request, Response response, JSONObject body, HashMap<String, String> headers, DiscordUser user) {
		String newValue = headers.get("newValue");
		String databaseKey = headers.get("databaseKey");
		String guildId = headers.get("guild");
		@SuppressWarnings("unused")
		Guild guild;
		try { guild = Bot.jda.getGuildById(guildId); } catch (Exception e) { return "bing bong request gone"; }
		DashboardValue value = Dashboard.getDashboardValueFromKey(databaseKey);
		if (value != null) {
			value.save(databaseKey, newValue, guildId);
		}
		return "ding dong done";
	}

	@Override
	public String url() {
		return "updatevalue";
	}
	
	@Override
	public String[] requiredParameters() {
		return new String[] { "databaseKey", "newValue" };
	}
	
	@Override
	public boolean requiresLogin() {
		return false;
	}
}