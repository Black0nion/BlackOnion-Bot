package com.github.black0nion.blackonionbot.api.impl.post;

import java.util.HashMap;

import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.api.BlackSession;
import com.github.black0nion.blackonionbot.api.routes.IPostRoute;

import spark.Request;
import spark.Response;

public class Test implements IPostRoute {

	@Override
	public String handle(final Request request, final Response response, final JSONObject body, final HashMap<String, String> headers, final BlackSession session, DiscordUser user) {
		if (session != null) return "oh hello there, " + session.getUser().getUser().getFullUsername();
		else return "oh hello there";
	}

	@Override
	public String url() {
		return "hi";
	}

	@Override
	public boolean requiresLogin() {
		return true;
	}
}