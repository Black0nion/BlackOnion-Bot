package com.github.black0nion.blackonionbot.API;

import java.util.HashMap;

import javax.annotation.Nullable;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.utils.Time;

import spark.Request;
import spark.Response;

public abstract class BlackRequest {
    public abstract String handle(Request request, Response response, JSONObject body, HashMap<String, String> headers, @Nullable BlackSession user);

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

	protected String exception(Throwable e) {
		return "{\"error\":\"" + e.getMessage() + "\"}";
	}

	protected String exception(String text) {
		return "{\"error\":\"" + text + "\"}";
	}

	protected String exception(String text, int code, Response response) {
		response.status(code);
		return "{\"error\":\"" + text + "\"}";
	}
}