package com.github.black0nion.blackonionbot.rest.impl.post;

import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import com.github.black0nion.blackonionbot.rest.api.IPostRoute;
import com.github.black0nion.blackonionbot.rest.sessions.RestSession;
import io.javalin.http.Context;
import org.json.JSONObject;

import javax.annotation.Nonnull;

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
	public Object handle(Context ctx, JSONObject body, RestSession session, DiscordUser user) throws Exception {
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
