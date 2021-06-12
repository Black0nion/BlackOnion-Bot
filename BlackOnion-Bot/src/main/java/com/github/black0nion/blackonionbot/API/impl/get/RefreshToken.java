package com.github.black0nion.blackonionbot.API.impl.get;

import java.util.HashMap;

import javax.annotation.Nullable;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.API.GetRequest;
import com.github.black0nion.blackonionbot.utils.DiscordUser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import spark.Request;
import spark.Response;

public class RefreshToken extends GetRequest {

    @Override
    public String handle(final Request request, final Response response, final JSONObject body, final HashMap<String, String> headers, final DiscordUser user) {
	final JSONObject tokenResponse = refreshTokenJSON(body.getString("refresh_token"));
	if (tokenResponse == null) {
	    response.status(500);
	    return new JSONObject().put("success", false).toString();
	} else {
	    if (tokenResponse.has("access_token") && tokenResponse.has("refresh_token")) return new JSONObject().put("success", true).put("access_token", tokenResponse.getString("access_token")).put("refresh_token", tokenResponse.getString("refresh_token")).toString();
	    else {
		response.status(500);
		return new JSONObject().put("success", false).put("response", tokenResponse).toString();
	    }
	}
    }

    @Override
    public String url() {
	return "refreshtoken";
    }

    @Override
    public String[] requiredParameters() {
	return new String[] { "refresh_token" };
    }

    @Nullable
    public static String refreshToken(final String refreshToken) {
	final JSONObject obj = refreshTokenJSON(refreshToken);
	if (obj.has("access_token") && obj.has("refresh_token")) return obj != null ? obj.getString("access_token") + ":" + obj.getString("refresh_token") : null;
	return null;
    }

    public static JSONObject refreshTokenJSON(final String refreshToken) {
	try {
	    Unirest.setTimeouts(0, 0);
	    final HttpResponse<String> response = Unirest.post("https://discord.com/api/oauth2/token").header("Content-Type", "application/x-www-form-urlencoded").header("Cookie", "__cfduid=d4a1e0b186f3b361a793fa34d489279d31609842531").field("refresh_token", refreshToken).field("client_id", "795958787865313341").field("client_secret", "CbpXY9haCRL8dV0bYALXbjUvVo29DYsp").field("grant_type", "refresh_token").field("redirect_uri", "https://2nd.hoferweb.net/pages/dashboard/dashboard.html").field("scope", "identify").asString();
	    return new JSONObject(response.getBody());
	} catch (final Exception e) {
	    return null;
	}
    }

}
