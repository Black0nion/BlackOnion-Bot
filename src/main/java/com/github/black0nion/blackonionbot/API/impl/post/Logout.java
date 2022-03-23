package com.github.black0nion.blackonionbot.api.impl.post;

import java.util.HashMap;

import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import org.bson.Document;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.api.BlackSession;
import com.github.black0nion.blackonionbot.api.routes.IPostRoute;
import com.mongodb.client.model.Filters;

import spark.Request;
import spark.Response;

import static com.github.black0nion.blackonionbot.api.API.exception;

/**
 * Logs out a user from a session (deletes their session id from the database
 * and revoke token)
 *
 * @author _SIM_
 */
public class Logout implements IPostRoute {

	@Override
	public String url() {
		return "logout";
	}

	@Override
	public String handle(final Request request, final Response response, final JSONObject body, final HashMap<String, String> headers, final BlackSession session, DiscordUser user) {
		final Document doc = BlackSession.collection.find(Filters.eq("sessionid", request.headers("sessionid"))).first();
		if (doc != null) {
			BlackSession.collection.deleteOne(Filters.eq("sessionid", request.headers("sessionid")));
			return "";
		}
		return exception("Session not found", 401, response);
	}

	@Override
	public boolean requiresLogin() {
		return true;
	}
}