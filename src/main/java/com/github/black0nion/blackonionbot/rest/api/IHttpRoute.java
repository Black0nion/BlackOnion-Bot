package com.github.black0nion.blackonionbot.rest.api;

import com.github.black0nion.blackonionbot.rest.sessions.RestSession;
import com.github.black0nion.blackonionbot.misc.enums.CustomPermission;
import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import org.json.JSONObject;

import javax.annotation.Nonnull;

public interface IHttpRoute extends IRoute {
	Object handle(Context ctx, JSONObject body, RestSession session, DiscordUser user) throws Exception;

	@Nonnull
	HandlerType type();

	default String[] requiredHeaders() {
		return new String[0];
	}

	default String[] requiredBodyParameters() {
		return new String[0];
	}

	default boolean isJson() {
		return true;
	}

	default boolean requiresLogin() {
		return false;
	}

	default CustomPermission[] requiredCustomPermissions() {
		return new CustomPermission[0];
	}
}
