package com.github.black0nion.blackonionbot.RestAPI.impl.post;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.RestAPI.PostRequest;
import com.github.black0nion.blackonionbot.utils.DiscordUser;

import spark.Request;
import spark.Response;

public class Login implements PostRequest {

	@Override
	public String handle(Request request, Response response, JSONObject body, DiscordUser user) {
		
		return null;
	}

	@Override
	public String url() {
		// TODO Auto-generated method stub
		return null;
	}

}
