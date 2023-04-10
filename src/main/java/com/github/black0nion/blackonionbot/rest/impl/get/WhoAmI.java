package com.github.black0nion.blackonionbot.rest.impl.get;

import com.github.black0nion.blackonionbot.oauth.OAuthUser;
import com.github.black0nion.blackonionbot.rest.api.IGetRoute;
import com.github.black0nion.blackonionbot.rest.sessions.RestSession;
import io.javalin.http.Context;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import javax.annotation.Nonnull;

public class WhoAmI implements IGetRoute {

	@Override
	public Object handle(Context ctx, JSONObject body, @Nullable RestSession session, OAuthUser user) throws Exception {
		return user.getUserAsJson();
	}

	@Override
	public @Nonnull String url() {
		return "whoami";
	}

	@Override
	public boolean requiresLogin() {
		return true;
	}
}