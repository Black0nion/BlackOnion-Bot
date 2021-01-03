package com.github.ahitm_2020_2025.blackonionbot.RestAPI.impl.post;

import org.json.JSONObject;

import com.github.ahitm_2020_2025.blackonionbot.Logger;
import com.github.ahitm_2020_2025.blackonionbot.RestAPI.PostRequest;
import com.github.ahitm_2020_2025.blackonionbot.bot.BotManager;
import com.github.ahitm_2020_2025.blackonionbot.enums.LogOrigin;
import com.github.ahitm_2020_2025.blackonionbot.utils.BotUser;
import spark.Request;
import spark.Response;

public class ChangePrefix implements PostRequest {

	@Override
	public String handle(Request request, Response response, JSONObject body, BotUser user) {
		BotManager.updatePrefix(body.getString("prefix"));
		Logger.logWarning("New Prefix: " + body.getString("prefix") + " | Changed by user " + user.getOriginalName(), LogOrigin.BOT);
		return new JSONObject()
				.put("success", true)
				.put("new_prefix", body.getString("prefix"))
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
