package com.github.black0nion.blackonionbot.API.impl.get;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.API.GetRequest;
import com.github.black0nion.blackonionbot.utils.DiscordUser;

import spark.Request;
import spark.Response;
import spark.Spark;
import spark.routematch.RouteMatch;

public class Paths implements GetRequest {

	@Override
	public String handle(Request request, Response response, JSONObject body, DiscordUser user) {
		JSONObject obj = new JSONObject();
		
		List<String> routes = new ArrayList<String>();
		
		for (RouteMatch route : Spark.routes()) {
			routes.add(route.getMatchUri());
		}
		obj.put("paths", routes);
		
		return obj.toString();
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
	public boolean requiresAdmin() {
		return true;
	}

}
