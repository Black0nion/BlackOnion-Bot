package com.github.black0nion.blackonionbot.RestAPI.impl.get;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.RestAPI.GetRequest;
import com.github.black0nion.blackonionbot.utils.DiscordUser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import spark.Request;
import spark.Response;

public class RefreshToken implements GetRequest {

	@Override
	public String handle(Request request, Response response, JSONObject body, DiscordUser user) {
		JSONObject tokenResponse = refreshTokenJSON(body.getString("refresh_token"));
		if (tokenResponse == null) {
			response.status(500);
			return new JSONObject().put("success", false).toString();
		} else {
			if (tokenResponse.has("access_token") && tokenResponse.has("refresh_token")) {
				return new JSONObject().put("success", true).put("access_token", tokenResponse.getString("access_token")).put("refresh_token", tokenResponse.getString("refresh_token")).toString();
			} else {
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
		return new String[] {"refresh_token"};
	}
	
	public static String refreshToken(String refreshToken) {
		JSONObject obj = refreshTokenJSON(refreshToken);
		return obj != null ? obj.getString("access_token") + ":" + obj.getString("refresh_token") : null;
	}
	
	public static JSONObject refreshTokenJSON(String refreshToken) {
		try {
			Unirest.setTimeouts(0, 0);
			HttpResponse<String> response = Unirest.post("https://discord.com/api/oauth2/token")
			  .header("Content-Type", "application/x-www-form-urlencoded")
			  .header("Cookie", "__cfduid=d4a1e0b186f3b361a793fa34d489279d31609842531")
			  .field("refresh_token", refreshToken)
			  .field("client_id", "795958787865313341")
			  .field("client_secret", "CbpXY9haCRL8dV0bYALXbjUvVo29DYsp")
			  .field("grant_type", "refresh_token")
			  .field("redirect_uri", "https://2nd.hoferweb.net/pages/dashboard/dashboard.html")
			  .field("scope", "identify")
			  .asString();
			return new JSONObject(response.getBody());
		} catch (Exception e) {
			return null;
		}
	}

}
