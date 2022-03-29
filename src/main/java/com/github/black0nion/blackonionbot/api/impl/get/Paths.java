package com.github.black0nion.blackonionbot.api.impl.get;

import java.util.HashMap;
import java.util.stream.Collectors;

import com.github.black0nion.blackonionbot.misc.CustomPermission;
import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.api.BlackSession;
import com.github.black0nion.blackonionbot.api.routes.IGetRoute;

import spark.Request;
import spark.Response;
import spark.Spark;
import spark.routematch.RouteMatch;

public class Paths implements IGetRoute {

	@Override
	public String handle(final Request request, final Response response, final JSONObject body, final HashMap<String, String> headers, final BlackSession session, DiscordUser user) {
		return new JSONArray(Spark.routes().stream().map(RouteMatch::getMatchUri).collect(Collectors.toList())).toString();
	}

	@Override
	public String url() {
		return "paths";
	}

	@Override
	public boolean requiresLogin() {
		return true;
	}

	@Override
	public CustomPermission[] requiredCustomPermissions() {
		return new CustomPermission[] { CustomPermission.ADMIN };
	}
}