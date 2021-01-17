package com.github.black0nion.blackonionbot.RestAPI;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.utils.DiscordUser;

import spark.Request;
import spark.Response;

public interface GetRequest {
	String handle(Request request, Response response, JSONObject body, DiscordUser user);
	
	default boolean requiresLogin() {
		return false;
	}
	
	default boolean requiresAdmin() {
		return false;
	}
	
	default boolean isJson() {
		return true;
	}
	
	String url();
	
	default String[] requiredParameters() {
		return new String[] {};
	}
}
