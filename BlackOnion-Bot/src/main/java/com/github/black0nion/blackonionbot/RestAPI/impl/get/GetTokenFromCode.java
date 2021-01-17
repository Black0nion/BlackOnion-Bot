package com.github.black0nion.blackonionbot.RestAPI.impl.get;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.RestAPI.GetRequest;
import com.github.black0nion.blackonionbot.utils.DiscordUser;
import com.github.black0nion.blackonionbot.utils.Utils;

import spark.Request;
import spark.Response;

public class GetTokenFromCode implements GetRequest {

	@SuppressWarnings("deprecation")
	@Override
	public String handle(Request request, Response response, JSONObject body, DiscordUser user) {
		String code = body.getString("code");
		JSONObject obj = new JSONObject(Utils.getTokenFromCode(code).getBody());
		return new JSONObject().put("access_token", obj.getString("access_token"))
				.put("refresh_token", obj.getString("refresh_token")).toString();
	}

	@Override
	public String url() {
		return "tokenfromcode";
	}
	
	@Override
	public String[] requiredParameters() {
		return new String[] {"code"};
	}

}
