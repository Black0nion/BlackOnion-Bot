package com.github.black0nion.blackonionbot.api.impl.post;

import java.io.IOException;
import java.util.HashMap;
import java.util.InputMismatchException;

import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.api.BlackSession;
import com.github.black0nion.blackonionbot.oauth.OAuthUtils;
import com.github.black0nion.blackonionbot.api.routes.IPostRoute;

import spark.Request;
import spark.Response;

import static com.github.black0nion.blackonionbot.api.API.exception;

public class Login implements IPostRoute {

	@Override
	public String handle(final Request request, final Response response, final JSONObject body, final HashMap<String, String> headers, final BlackSession session, DiscordUser user) {
		try {
			final String code = request.headers("code");
			final String loginWithDiscord = OAuthUtils.loginWithDiscord(code);
			if (loginWithDiscord == null) {
				return exception("Login failed", 401, response);
			} else return loginWithDiscord;
		} catch (final InputMismatchException | IOException e) {
			return exception(e.getMessage(), 401, response);
		}
	}

	@Override
	public String url() {
		return "login";
	}

	@Override
	public boolean requiresLogin() {
		return false;
	}

	@Override
	public String[] requiredParameters() {
		return new String[] { "code" };
	}
}