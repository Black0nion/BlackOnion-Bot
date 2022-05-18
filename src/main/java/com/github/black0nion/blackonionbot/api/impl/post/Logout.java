package com.github.black0nion.blackonionbot.api.impl.post;

import com.github.black0nion.blackonionbot.api.routes.IPostRoute;
import com.github.black0nion.blackonionbot.api.sessions.RestSession;
import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import io.javalin.http.Context;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Logs out an user from a session (deletes their session id from the database
 * and revoke token)
 */
public class Logout implements IPostRoute {

	@Override
	public @Nonnull String url() {
		return "logout";
	}

	@Override
	public Object handle(Context ctx, JSONObject body, Map<String, String> headers, RestSession session, DiscordUser user) throws Exception {
		session.logout();
		ctx.status(204);
		return "";
	}

	@Override
	public boolean requiresLogin() {
		return true;
	}

	@Override
	public String[] requiredHeaders() {
		return new String[] { "sessionid" };
	}
}