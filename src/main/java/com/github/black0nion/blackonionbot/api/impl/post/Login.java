package com.github.black0nion.blackonionbot.api.impl.post;

import com.github.black0nion.blackonionbot.api.BlackSession;
import com.github.black0nion.blackonionbot.api.routes.IPostRoute;
import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import com.github.black0nion.blackonionbot.oauth.OAuthUtils;
import io.javalin.http.Context;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.util.Map;

public class Login implements IPostRoute {

	@Override
	public Object handle(Context ctx, JSONObject body, Map<String, String> headers, @Nullable BlackSession session,
			DiscordUser user) throws Exception {
		return OAuthUtils.loginWithDiscord(ctx.header("code"));
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
		return new String[]{"code"};
	}
}
