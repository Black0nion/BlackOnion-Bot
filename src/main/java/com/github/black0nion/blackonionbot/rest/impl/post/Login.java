package com.github.black0nion.blackonionbot.rest.impl.post;

import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import com.github.black0nion.blackonionbot.oauth.OAuthHandler;
import com.github.black0nion.blackonionbot.rest.api.IPostRoute;
import com.github.black0nion.blackonionbot.rest.sessions.RestSession;
import com.github.black0nion.blackonionbot.utils.Utils;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.util.InputMismatchException;

public class Login implements IPostRoute {

	private final OAuthHandler handler;

	public Login(OAuthHandler handler) {
		this.handler = handler;
	}

	@Override
	public Object handle(Context ctx, JSONObject body, @Nullable RestSession session, DiscordUser user) throws Exception {
		return Utils.replaceException(() -> handler.loginWithDiscord(ctx.header("code")), InputMismatchException.class, BadRequestResponse.class);
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
