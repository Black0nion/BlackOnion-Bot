package com.github.black0nion.blackonionbot.api.impl.post;

import com.github.black0nion.blackonionbot.api.BlackSession;
import com.github.black0nion.blackonionbot.api.routes.IPostRoute;
import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import com.mongodb.client.model.Filters;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Logs out a user from a session (deletes their session id from the database
 * and revoke token)
 *
 * @author _SIM_
 */
public class Logout implements IPostRoute {

	@Override
	public @Nonnull String url() {
		return "logout";
	}

	@Override
	public Object handle(Context ctx, JSONObject body, Map<String, String> headers, @Nullable BlackSession session,
			DiscordUser user) throws Exception {
		Bson filter = Filters.eq("sessionid", assertMatches(ctx.header("sessionid"), BlackSession.SESSIONID_REGEX));
		final Document doc = BlackSession.collection.find(filter).first();

		if (doc != null) {
			BlackSession.collection.deleteOne(Filters.eq("sessionid", ctx.header("sessionid")));
			return "";
		}
		throw new UnauthorizedResponse("Invalid session id");
	}

	@Override
	public boolean requiresLogin() {
		return true;
	}

	@Override
	public String[] requiredHeaders() {
		return new String[]{"sessionid"};
	}
}
