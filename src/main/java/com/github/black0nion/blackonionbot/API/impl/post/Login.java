package com.github.black0nion.blackonionbot.API.impl.post;

import java.util.HashMap;

import javax.annotation.Nullable;

import org.bson.Document;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.API.BlackSession;
import com.github.black0nion.blackonionbot.API.OAuthUtils;
import com.github.black0nion.blackonionbot.API.PostRequest;
import com.github.black0nion.blackonionbot.utils.Trio;
import com.mongodb.client.model.Filters;

import spark.Request;
import spark.Response;

public class Login extends PostRequest {

    @Override
    public String handle(final Request request, final Response response, final JSONObject body, final HashMap<String, String> headers, final BlackSession user) {
	try {
	    final String code = request.headers("code");
	    final String loginWithDiscord = loginWithDiscord(code);
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

    /**
     * call once to generate token from code and save that shit, only on first login
     * with discord, on reconnect on the same PC (session) use
     * {@link #loginToSession(String)}!
     *
     * @param  code the code discord gave you
     * @return      session id
     */
    @Nullable
    public static String loginWithDiscord(final String code) {
	try {
	    final Trio<String, String, Integer> response = OAuthUtils.getTokensFromCode(code);
	    if (response == null) return null;
	    else {
		final String accessToken = response.getFirst();
		final String refreshToken = response.getSecond();
		final int expiresIn = response.getThird();
		final Document find = BlackSession.collection.find(Filters.and(Filters.eq("access_token", accessToken), Filters.eq("refresh_token", refreshToken), Filters.exists("sessionid"))).first();
		if (find != null) return find.getString("sessionid");
		final String newSessionId = BlackSession.generateSessionId();
		BlackSession.collection.insertOne(new Document().append("sessionid", newSessionId).append("access_token", accessToken).append("refresh_token", refreshToken).append("expires_in", expiresIn));
		return newSessionId;
	    }
	} catch (final Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }
}