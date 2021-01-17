package com.github.black0nion.blackonionbot.RestAPI.impl.post;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.RestAPI.PostRequest;
import com.github.black0nion.blackonionbot.commands.bot.ActivityCommand;
import com.github.black0nion.blackonionbot.utils.DiscordUser;

import spark.Request;
import spark.Response;

public class Activity implements PostRequest {

	@Override
	public String handle(Request request, Response response, JSONObject body, DiscordUser user) {
		String newActivityType = body.getString("activityType");
		String newActivity = body.getString("activity");
		if (ActivityCommand.getActivity(newActivityType, newActivity) == null) {
			response.status(400);
			return new JSONObject().put("success", false).put("reason", 400).toString();
		}
		return new JSONObject().put("success", true).put("new_activity_type", newActivityType).put("new_activity", newActivity).toString();
	}

	@Override
	public String url() {
		return "activity";
	}
	
	@Override
	public boolean requiresAdmin() {
		return true;
	}
	
	@Override
	public boolean requiresLogin() {
		return true;
	}
	
	@Override
	public String[] requiredParameters() {
		return new String[] { "activityType", "activity" };
	}

}
