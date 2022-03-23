package com.github.black0nion.blackonionbot.api.routes;

import java.util.HashMap;

import javax.annotation.Nullable;

import com.github.black0nion.blackonionbot.api.BlackSession;
import com.github.black0nion.blackonionbot.misc.CustomPermission;
import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.utils.Time;

import spark.Request;
import spark.Response;

public interface IRoute {
	Object handle(Request request, Response response, JSONObject body, HashMap<String, String> headers, @Nullable BlackSession session, DiscordUser user);

	String url();

	Time rateLimit();

	default boolean requiresLogin() {
		return false;
	}

	default CustomPermission[] requiredCustomPermissions() {
		return new CustomPermission[0];
	}

	default boolean isJson() {
		return true;
	}

	default String[] requiredParameters() {
		return new String[0];
	}

	default String[] requiredBodyParameters() {
		return new String[0];
	}

	HttpMethod type();
}