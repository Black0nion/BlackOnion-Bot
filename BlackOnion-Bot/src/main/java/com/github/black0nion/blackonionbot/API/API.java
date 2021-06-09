package com.github.black0nion.blackonionbot.API;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;
import org.reflections.Reflections;

import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.systems.dashboard.BlackSession;
import com.github.black0nion.blackonionbot.systems.dashboard.DiscordLogin;
import com.github.black0nion.blackonionbot.systems.logging.Logger;
import com.github.black0nion.blackonionbot.utils.DiscordUser;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.utils.ValueManager;
import com.google.common.util.concurrent.RateLimiter;
import com.mongodb.client.model.Filters;

import spark.Spark;

public class API {

    private static ArrayList<PostRequest> postRequests = new ArrayList<>();
    private static ArrayList<GetRequest> getRequests = new ArrayList<>();

    private static final HashMap<String, RateLimiter> rateLimiters = new HashMap<>();

    @Reloadable("api")
    public static void init() {
	postRequests.clear();
	getRequests.clear();
	final int port = ValueManager.getInt("api_port");
	Spark.stop();
	Spark.awaitStop();
	Spark.port(port != 0 ? port : 187);

	final Reflections reflections = new Reflections(API.class.getPackage().getName());
	// ------------------WebSockets------------------
	final Set<Class<? extends WebSocketEndpoint>> websocketsClasses = reflections.getSubTypesOf(WebSocketEndpoint.class);

	for (final Class<? extends WebSocketEndpoint> websockets : websocketsClasses) {
	    WebSocketEndpoint endpoint;
	    try {
		endpoint = websockets.getConstructor().newInstance();
		Spark.webSocket("/" + endpoint.getRoute(), websockets);
	    } catch (final Exception e) {
		e.printStackTrace();
	    }
	}
	Spark.init();
	// -----------------Get Requests-----------------
	final Set<Class<? extends GetRequest>> getRequestClasses = reflections.getSubTypesOf(GetRequest.class);

	for (final Class<?> req : getRequestClasses) {
	    try {
		getRequests.add((GetRequest) req.getConstructor().newInstance());
	    } catch (final Exception e) {
		e.printStackTrace();
	    }
	}
	// ----------------Post Requests-----------------
	final Set<Class<? extends PostRequest>> postRequestClasses = reflections.getSubTypesOf(PostRequest.class);

	for (final Class<?> req : postRequestClasses) {
	    try {
		postRequests.add((PostRequest) req.getConstructor().newInstance());
	    } catch (final Exception e) {
		e.printStackTrace();
	    }
	}
	// ----------------------------------------------

	// Error handling
	Spark.internalServerError((request, response) -> {
	    response.header("Access-Control-Allow-Origin", "*");
	    response.type("application/json");
	    response.status(500);
	    return new JSONObject().put("success", false).put("reason", 500).toString();
	});

	Spark.notFound((request, response) -> {
	    response.header("Access-Control-Allow-Origin", "*");
	    response.type("application/json");
	    response.status(404);
	    return new JSONObject().put("success", false).put("reason", 404).toString();
	});

	// ----------------Post Requests-----------------
	for (final PostRequest req : postRequests) {
	    if (req.url() == null) {
		API.logError("Path is null: " + req.getClass().getName());
		continue;
	    }
	    final String url = "/api/" + req.url();
	    post(url, (request, response) -> {
		final String ip = request.headers("X-Real-IP") != null ? request.headers("X-Real-IP") : request.ip();
		try {
		    // RATE LIMITING:
		    if (rateLimiters.containsKey(ip)) {
			final RateLimiter limiter = rateLimiters.get(ip);
			if (!limiter.tryAcquire()) {
			    response.status(429);
			    return new JSONObject().put("success", false).put("error", "ratelimited").toString();
			}
		    } else {
			rateLimiters.put(ip, RateLimiter.create(2));
		    }
		    response.header("Access-Control-Allow-Origin", "*");
		    JSONObject body = new JSONObject();
		    if (req.requiredBodyParameters().length != 0) {
			body = new JSONObject(request.body());
		    }

		    final HashMap<String, String> headers = new HashMap<>();
		    request.headers().forEach(head -> {
			headers.put(head, request.headers(head));
		    });

		    final String sessionid = headers.get("sessionid");
		    if (req.requiresLogin() && sessionid == null) {
			response.status(401);
			return new JSONObject().put("success", false).put("reason", 401).toString();
		    }

		    if (req.requiresLogin() && sessionid == null) {
			response.status(401);
			return new JSONObject().put("success", false).put("reason", 401).toString();
		    }

		    // all the information we got saved about the session
		    final Document sessionInformation = BlackSession.collection.find(Filters.eq("sessionid", sessionid)).first();
		    // if the request requires login, this won't be null
		    DiscordLogin login = null;
		    if (req.requiresLogin()) {
			final JSONObject userinfo = Utils.getUserInfoFromToken(sessionInformation.getString("access_token"));
			login = DiscordLogin.success(userinfo);

			// TODO: check user permissions
			if (login == null || !login.success()) {
			    response.status(401);
			    return new JSONObject().put("success", false).put("reason", 401).toString();
			}
		    }

		    if (!request.headers().containsAll(Arrays.asList(req.requiredParameters()))) {
			response.status(400);
			response.type("application/json");
			return new JSONObject().put("success", false).put("reason", 400).put("detailedReason", "missingParameters").toString();
		    }

		    API.logInfo("Answered POST request (Path: " + url + ") from: " + ip + " with header: " + body.toString());

		    response.type("text/plain");
		    if (req.isJson()) {
			response.type("application/json");
		    }

		    for (final String s : req.requiredBodyParameters()) {
			if (!body.has(s)) {
			    response.status(400);
			    return new JSONObject().put("success", false).put("reason", 400).toString();
			}
		    }

		    return req.handle(request, response, body, headers, login != null ? login.getUser() : null);
		} catch (final Exception e) {
		    API.logInfo("Answered malformed POST request (Path: " + url + ") from: " + ip);
		    if (!(e instanceof JSONException)) {
			e.printStackTrace();
		    }

		    response.type("application/json");
		    if (e instanceof JSONException) {
			response.status(400);
			return new JSONObject().put("success", false).put("reason", 400).put("detailedReason", "jsonException").toString();
		    } else {
			response.status(500);
			return new JSONObject().put("success", false).put("reason", 500).put("detailedReason", "exception").toString();
		    }
		}
	    });
	}
	// ----------------------------------------------
	// -----------------Get Requests-----------------

	for (final GetRequest req : getRequests) {
	    if (req.url() == null) {
		API.logError("Path is null: " + req.getClass().getName());
		continue;
	    }
	    final String url = "/api/" + req.url();
	    get(url, (request, response) -> {
		try {
		    response.header("Access-Control-Allow-Origin", "*");
		    JSONObject body = new JSONObject();
		    if (req.requiredParameters().length != 0) {
			body = new JSONObject(request.body());
		    }
		    final JSONObject headers = new JSONObject();
		    request.headers().forEach(head -> {
			headers.put(head, request.headers(head));
		    });
		    API.logInfo("Answered GET request (Path: " + url + ") from: " + request.ip() + " with header: " + body.toString());

		    if (req.isJson()) {
			response.type("application/json");
		    }

		    String token = null;
		    JSONObject userInfo = null;
		    if (req.requiresLogin()) {
			if (!headers.has("token")) {
			    response.status(401);
			    return new JSONObject().put("success", false).put("reason", 401).toString();
			}

			token = headers.getString("token");

			if (!Utils.isDiscordUser(token)) {
			    response.status(401);
			    return new JSONObject().put("success", false).put("reason", 401).toString();
			}
			// TODO: admin check!
			if (req.requiresAdmin()) {
			    response.status(403);
			    return new JSONObject().put("success", false).put("reason", 403).toString();
			}

			userInfo = Utils.getUserInfoFromToken(token);
		    }

		    for (final String s : req.requiredParameters()) {
			if (!body.has(s)) {
			    response.status(400);
			    return new JSONObject().put("success", false).put("reason", 400).toString();
			}
		    }

		    final DiscordUser user = (userInfo != null ? new DiscordUser(Long.parseLong(userInfo.getString("id")), userInfo.getString("username"), userInfo.getString("avatar"), userInfo.getString("discriminator"), userInfo.getString("locale").toUpperCase(), userInfo.getBoolean("mfa_enabled")) : null);
		    return req.handle(request, response, null, user);
		} catch (final JSONException e) {
		    response.status(400);
		    response.type("application/json");
		    return new JSONObject().put("success", false).put("reason", 400).put("detailedReason", "jsonException: " + e.getMessage()).toString();
		}
	    });
	}
	// ----------------------------------------------
    }

    public static void logInfo(final String logInput) {
	Logger.logInfo(logInput, LogOrigin.API);
    }

    public static void logWarning(final String logInput) {
	Logger.logWarning(logInput, LogOrigin.API);
    }

    public static void logError(final String logInput) {
	Logger.logError(logInput, LogOrigin.API);
    }
}
