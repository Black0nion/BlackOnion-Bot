package com.github.black0nion.blackonionbot.RestAPI.impl.post;

import java.util.HashMap;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.RestAPI.PostRequest;
import com.github.black0nion.blackonionbot.utils.DiscordUser;

import spark.Request;
import spark.Response;

public class UpdateValue implements PostRequest {

	@Override
	public String handle(Request request, Response response, JSONObject body, HashMap<String, String> headers, DiscordUser user) {
		String newValue = headers.get("newValue");
		String databaseKey = headers.get("datbaseKey");
		
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