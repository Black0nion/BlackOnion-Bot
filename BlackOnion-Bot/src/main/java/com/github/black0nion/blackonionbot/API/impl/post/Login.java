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
	    final BlackSession newSession = new BlackSession();
	    if (!newSession.loginWithDiscord(code)) {
		response.status(401);
		return new JSONObject("detailedReason", "Invalid Code").toString();
	    } else return newSession.getSessionId();
	} catch (final Exception e) {
	    e.printStackTrace();
	    response.status(500);
	    return new JSONObject().put("detailedReason", "Server Error").toString();
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