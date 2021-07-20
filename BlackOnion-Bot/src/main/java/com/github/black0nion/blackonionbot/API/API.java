package com.github.black0nion.blackonionbot.API;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONException;
import org.json.JSONObject;
import org.reflections.Reflections;

import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.utils.BlackRateLimiter;
import com.github.black0nion.blackonionbot.utils.ValueManager;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import spark.Route;
import spark.Spark;

public class API {

    private static final HashMap<String, BlackRequest> requests = new HashMap<>();
    private static final List<String> websocketEndpoints = new ArrayList<>();

    private static final HashMap<String, BlackRateLimiter> rateLimiters = new HashMap<>();

    private static final MongoCollection<Document> collection = MongoDB.botDatabase.getCollection("dashboard-sessions");

    @Reloadable("ratelimits")
    public static void clearRateLimits() {
	rateLimiters.clear();
    }

    @Reloadable("api")
    public static void init() {
	requests.clear();
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
		Spark.webSocket("/ws/" + endpoint.getRoute(), websockets);
		websocketEndpoints.add("/ws/" + endpoint.getRoute());
	    } catch (final Exception e) {
		e.printStackTrace();
	    }
	}
	Spark.init();

	// -----------------Map Requests-----------------
	final Set<Class<? extends BlackRequest>> requestClasses = reflections.getSubTypesOf(BlackRequest.class);

	for (final Class<?> req : requestClasses) {
	    try {
		if (req.getSuperclass() != BlackRequest.class) {
		    final BlackRequest newInstance = (BlackRequest) req.getConstructor().newInstance();
		    requests.put("/api/" + newInstance.url(), newInstance);
		}
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
	    logError("Some internal server error happened! URL: " + request.url());
	    return new JSONObject().put("reason", 500).toString();
	});

	final String ratelimited = new JSONObject().put("error", "ratelimited").toString();
	Spark.before((req, res) -> {
	    final String ip = req.headers("X-Real-IP") != null ? req.headers("X-Real-IP") : req.ip();
	    logInfo("IP " + ip + " tried to connect to " + req.url());
	    if (!requests.containsKey(req.uri()) && !websocketEndpoints.contains(req.uri())) {
	    } else {
		final BlackRequest request = requests.get(req.uri());
		// RATE LIMITING:
		if (rateLimiters.containsKey(ip)) {
		    final BlackRateLimiter limiter = rateLimiters.get(ip);
		    if (!limiter.tryAcquire()) {
			logWarning(ip + " got ratelimited! Sent " + limiter.getTooMany() + " too many requests!");
			res.status(429);
			Spark.halt(ratelimited);
		    }
		} else {
		    rateLimiters.put(ip, BlackRateLimiter.create(request.rateLimit()));
		}
	    }
	});

	// ----------------Post Requests-----------------
	for (final BlackRequest req : requests.values()) {
	    if (req.url() == null) {
		logError("Path is null: " + req.getClass().getName());
		continue;
	    }

	    final String url = "/api/" + req.url();
	    final Route route = (request, response) -> {
		final String ip = request.headers("X-Real-IP") != null ? request.headers("X-Real-IP") : request.ip();
		try {
		    response.header("Access-Control-Allow-Origin", "*");

		    final HashMap<String, String> headers = new HashMap<>();
		    request.headers().forEach(head -> {
			headers.put(head.toLowerCase(), request.headers(head));
		    });
		    if (!headers.keySet().containsAll(Arrays.asList(req.requiredParameters()))) {
			response.status(400);
			return "";
		    }

		    JSONObject body = new JSONObject();
		    if (req.requiredBodyParameters().length != 0) {
			body = new JSONObject(request.body());
		    }

		    BlackSession session = null;
		    if (req.requiresLogin()) {
			final String sessionId = request.headers("sessionid");
			final Bson filter = Filters.eq("sessionid", sessionId);
			final Document sessionInfo = collection.find(filter).first();
			if (sessionInfo != null) {
			    if (!(sessionInfo.containsKey("access_token") && sessionInfo.containsKey("refresh_token"))) {
				collection.deleteOne(filter);
				response.status(403);
				return new JSONObject().put("reason", "Your Session has no Tokens.").toString();
			    } else {
				// everything good, the session is existing and even has tokens, great
				session = new BlackSession(sessionId);
			    }
			}
			if (session == null || session.getUser() == null) {
			    response.status(401);
			    return new JSONObject().put("reason", "Not logged in.").toString();
			}
		    }

		    return req.handle(request, response, body, headers, session);
		} catch (final Exception e) {
		    logInfo("Answered malformed POST request (Path: " + url + ") from: " + ip);
		    if (!(e instanceof JSONException)) {
			e.printStackTrace();
		    }

		    response.type("application/json");
		    if (e instanceof JSONException) {
			response.status(400);
			return new JSONObject().put("reason", 400).put("reason", "jsonException").toString();
		    } else {
			response.status(500);
			return new JSONObject().put("reason", 500).put("reason", "exception").toString();
		    }
		}
	    };

	    final RequestType type = req.type();
	    if (type == RequestType.GET) {
		Spark.get(url, route);
	    } else if (type == RequestType.POST) {
		Spark.post(url, route);
	    } else {
		logError(type.name() + " has no Spark method reference!");
	    }
	}

	final String notFoundJson = new JSONObject().put("reason", 404).toString();
	final Route notFoundRoute = (request, response) -> {
	    response.header("Access-Control-Allow-Origin", "*");
	    response.type("application/json");
	    response.status(404);
	    return notFoundJson;
	};

	Spark.notFound(notFoundRoute);
	Spark.get("*", notFoundRoute);
	Spark.post("*", notFoundRoute);
    }

    public static void logInfo(final String logInput) {
	LogOrigin.API.info(logInput);
    }

    public static void logWarning(final String logInput) {
	LogOrigin.API.warn(logInput);
    }

    public static void logError(final String logInput) {
	LogOrigin.API.error(logInput);
    }
}