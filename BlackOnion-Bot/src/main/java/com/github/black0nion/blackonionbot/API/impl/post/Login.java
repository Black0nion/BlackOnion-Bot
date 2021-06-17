package com.github.black0nion.blackonionbot.API.impl.post;

import java.util.HashMap;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.API.PostRequest;
import com.github.black0nion.blackonionbot.utils.DiscordUser;
import com.github.black0nion.blackonionbot.utils.Utils;

import spark.Request;
import spark.Response;

public class Login extends PostRequest {

    // TODO: fix lol
    @Override
    public String handle(final Request request, final Response response, final JSONObject body, final HashMap<String, String> headers, final DiscordUser user) {
	try {
	    final JSONObject discordResponse = new JSONObject(Utils.getTokenFromCode(request.headers("code")).getBody());
	    if (!discordResponse.has("access_token")) {
		response.status(401);
		return new JSONObject().put("success", false).put("reason", 401).toString();
	    } else {
		// TODO: login

		System.out.println("test");
		return new JSONObject().put("success", true).toString();
	    }
	} catch (final Exception e) {
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

    @Override
    public String[] requiredParameters() {
	return new String[] { "code" };
    }
}
