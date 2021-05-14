package com.github.black0nion.blackonionbot.API.impl.get;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.API.API;
import com.github.black0nion.blackonionbot.API.GetRequest;
import com.github.black0nion.blackonionbot.utils.DiscordUser;
import com.github.black0nion.blackonionbot.utils.Utils;

import spark.Request;
import spark.Response;

public class GetTokenFromCode implements GetRequest {

	@SuppressWarnings("deprecation")
	@Override
	public String handle(Request request, Response response, JSONObject body, DiscordUser user) {
		String code = request.headers("code");
		if (code == null) {
			response.status(400);
			return new JSONObject().put("success", false).put("reason", "No code specified!").toString();
		}
		final String tokenResponse = Utils.getTokenFromCode(code).getBody();
		
		if (tokenResponse == null) {
			API.logError("No response from Discord!");
			response.status(400);
			return new JSONObject().put("success", false).put("reason", "Couldn't get a token from Discord!").toString();
		}
		
		JSONObject obj = new JSONObject(tokenResponse);
		
		if (!obj.has("access_token")) {
			API.logError("No access token found in " + obj.toString());
			response.status(400);
			return new JSONObject().put("success", false).put("reason", "Couldn't get a token from Discord!").toString();
		}
		
		Utils.getUserInfoFromToken(obj.getString("access_token"));
		
		return new JSONObject().put("access_token", obj.getString("access_token"))
				.put("refresh_token", obj.getString("refresh_token")).toString();
	}

	@Override
	public String url() {
		return "tokenfromcode";
	}
	
	@Override
	public boolean requiresLogin() {
		return false;
	}

}
