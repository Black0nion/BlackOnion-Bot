package com.github.black0nion.blackonionbot.API.impl.get;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.API.BlackSession;
import com.github.black0nion.blackonionbot.API.GetRequest;

import spark.Request;
import spark.Response;
import spark.Spark;
import spark.routematch.RouteMatch;

public class Paths extends GetRequest {

    @Override
    public String handle(final Request request, final Response response, final JSONObject body, final HashMap<String, String> headers, final BlackSession user) {
	final JSONObject obj = new JSONObject();

	final List<String> routes = new ArrayList<String>();

	for (final RouteMatch route : Spark.routes()) {
	    routes.add(route.getMatchUri());
	}
	obj.put("paths", routes);

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
    public boolean requiresAdmin() {
	return true;
    }
}