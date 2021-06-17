package com.github.black0nion.blackonionbot.API;

import java.util.HashMap;

import javax.annotation.Nullable;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.utils.DiscordUser;
import com.github.black0nion.blackonionbot.utils.Time;

import spark.Request;
import spark.Response;

public abstract class BlackRequest {
    public abstract String handle(Request request, Response response, JSONObject body, HashMap<String, String> headers, @Nullable DiscordUser user);

    public abstract String url();

    public abstract Time rateLimit();

    public boolean requiresLogin() {
	return false;
    }

    public boolean requiresAdmin() {
	return false;
    }

    public boolean isJson() {
	return true;
    }

    public String[] requiredParameters() {
	return new String[0];
    }

    public String[] requiredBodyParameters() {
	return new String[0];
    }

    public abstract RequestType type();
}