package com.github.black0nion.blackonionbot.RestAPI.impl.post;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.Logger;
import com.github.black0nion.blackonionbot.RestAPI.PostRequest;
import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.enums.LogOrigin;
import com.github.black0nion.blackonionbot.utils.DiscordUser;
import com.github.black0nion.blackonionbot.utils.ValueManager;

import spark.Request;
import spark.Response;

public class ChangePrefix implements PostRequest {

	@Override
	public String handle(Request request, Response response, JSONObject body, DiscordUser user) {
		final String newPrefix = body.getString("prefix");
		BotInformation.defaultPrefix = newPrefix;
		ValueManager.save("prefix", newPrefix);
		Logger.logWarning("New Prefix: " + newPrefix + " | Changed by user " + user.getUserName() + "#" + user.getDiscriminator(), LogOrigin.BOT);
		return new JSONObject()
				.put("success", true)
				.put("new_prefix", newPrefix)
				.toString();
	}

	@Override
	public String url() {
		return "prefix";
	}
	
	@Override
	public String[] requiredParameters() {
		return new String[] { "prefix" };
	}
	
	@Override
	public boolean isJson() {
		return true;
	}

}
