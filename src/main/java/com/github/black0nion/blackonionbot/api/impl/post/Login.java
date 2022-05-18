package com.github.black0nion.blackonionbot.api.impl.post;

import com.github.black0nion.blackonionbot.api.routes.IPostRoute;
import com.github.black0nion.blackonionbot.api.sessions.RestSession;
import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import com.github.black0nion.blackonionbot.oauth.OAuthUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.util.InputMismatchException;
import java.util.Map;

public class Login implements IPostRoute {

	@Override
	public Object handle(Context ctx, JSONObject body, Map<String, String> headers, @Nullable RestSession session, DiscordUser user) throws Exception {
		return Utils.replaceException(() -> OAuthUtils.loginWithDiscord(ctx.header("code")), InputMismatchException.class, BadRequestResponse.class);
	}

	@Override
	public @Nonnull String url() {
		return "login";
	}

	@Override
	public boolean requiresLogin() {
		return false;
	}

	@Override
	public String[] requiredHeaders() {
		return new String[] { "code" };
	}
}