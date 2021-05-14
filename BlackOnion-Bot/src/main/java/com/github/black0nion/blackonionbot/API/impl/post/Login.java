package com.github.black0nion.blackonionbot.API.impl.post;

import java.util.HashMap;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.API.PostRequest;
import com.github.black0nion.blackonionbot.systems.dashboard.SessionManager;
import com.github.black0nion.blackonionbot.utils.DiscordUser;
import com.github.black0nion.blackonionbot.utils.Utils;

import spark.Request;
import spark.Response;

public class Login implements PostRequest {
	
	@SuppressWarnings("deprecation")
	@Override
	public String handle(Request request, Response response, JSONObject body, HashMap<String, String> headers, DiscordUser user) {
		try {
			if (request.headers("code") != null) {
				JSONObject discordResponse = new JSONObject(Utils.getTokenFromCode(request.headers("code")).getBody());
				if (!discordResponse.has("access_token")) {
					response.status(401);
					return new JSONObject().put("success", true).put("reason", 401).toString();
				} else {
					SessionManager.generateSession(request.session(), discordResponse.getString("access_token"), discordResponse.getString("refresh_token"));
					return new JSONObject().put("success", true).toString();
				}
			} else if (request.headers("access_token") != null && request.headers("refresh_token") != null) {
				if (!Utils.isDiscordUser(request.headers("access_token"))) {
					response.status(401);
					return new JSONObject().put("success", false).put("reason", 401).toString();
				}
				
				SessionManager.generateSession(request.session(), request.headers("access_token"), request.headers("refresh_token"));
				return new JSONObject().put("success", true).toString();
			}
			
			response.status(400);
			return new JSONObject().put("success", false).put("reason", 400).toString();
		} catch (Exception e) {
			e.printStackTrace();
			response.status(500);
			return "";
		}
	}

	@Override
	public String url() {
		return "login";
	}
	
	@Override
	public boolean requiresLogin() {
		return false;
	}

}
