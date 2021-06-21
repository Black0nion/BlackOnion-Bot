package com.github.black0nion.blackonionbot.API.impl.post;

import java.util.HashMap;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.API.BlackSession;
import com.github.black0nion.blackonionbot.API.PostRequest;

import spark.Request;
import spark.Response;

public class Login extends PostRequest {

    // TODO: fix lol
    @Override
    public String handle(final Request request, final Response response, final JSONObject body, final HashMap<String, String> headers, final BlackSession user) {
	try {
	    final String code = request.headers("code");
	    final String loginWithDiscord = BlackSession.loginWithDiscord(code);
	    if (loginWithDiscord == null) {
		response.status(401);
		return "";
	    } else return loginWithDiscord;
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