/**
 *
 */
package com.github.black0nion.blackonionbot.API.impl.post;

import java.util.HashMap;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.API.PostRequest;
import com.github.black0nion.blackonionbot.utils.DiscordUser;

import spark.Request;
import spark.Response;

/**
 * @author _SIM_
 *
 */
public class Test implements PostRequest {

    @Override
    public String handle(final Request request, final Response response, final JSONObject body, final HashMap<String, String> headers, final DiscordUser user) {
	return "oh hello there";
    }

    @Override
    public String url() {
	return "hi";
    }

    @Override
    public boolean requiresLogin() {
	return false;
    }
}