package com.github.black0nion.blackonionbot.API;

import java.util.HashMap;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.utils.DiscordUser;

import spark.Request;
import spark.Response;

public interface PostRequest {
	String handle(Request request, Response response, JSONObject body, HashMap<String, String> headers, DiscordUser user);
	
	default boolean requiresLogin() {
		return true;
	}
	
	default boolean requiresAdmin() {
		return true;
	}
	
	default boolean isJson() {
		return true;
	}
	
	String url();
	
	default String[] requiredParameters() {
		return new String[] {};
	}
	
	default String[] requiredBodyParameters() {
		return new String[] {};
	}
}