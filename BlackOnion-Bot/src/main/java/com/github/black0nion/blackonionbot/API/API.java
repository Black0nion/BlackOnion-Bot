package com.github.black0nion.blackonionbot.API;

import java.util.ArrayList;
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
import com.github.black0nion.blackonionbot.systems.logging.Logger;
import com.github.black0nion.blackonionbot.utils.BlackRateLimiter;
import com.github.black0nion.blackonionbot.utils.ValueManager;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import spark.Route;
import spark.Spark;

public class API {

    private static HashMap<String, BlackRequest> requests = new HashMap<>();
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
		Spark.webSocket("/api/" + endpoint.getRoute(), websockets);
		websocketEndpoints.add("/api/" + endpoint.getRoute());
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
	    return new JSONObject().put("success", false).put("reason", 500).toString();
	});

	Spark.notFound((request, response) -> {
	    response.header("Access-Control-Allow-Origin", "*");
	    response.type("application/json");
	    response.status(404);
	    return new JSONObject().put("success", false).put("reason", 404).toString();
	});

	final String ratelimited = new JSONObject().put("success", false).put("error", "ratelimited").toString();
	Spark.before((req, res) -> {
	    if (!requests.containsKey(req.uri())) {
		if (websocketEndpoints.contains(req.uri())) return;
		Spark.halt(404, "notfound");
	    }

	    final BlackRequest request = requests.get(req.uri());
	    // RATE LIMITING:
	    final String ip = req.headers("X-Real-IP") != null ? req.headers("X-Real-IP") : req.ip();
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
		    JSONObject body = new JSONObject();
		    if (req.requiredBodyParameters().length != 0) {
			body = new JSONObject(request.body());
		    }

		    final HashMap<String, String> headers = new HashMap<>();
		    request.headers().forEach(head -> {
			headers.put(head, request.headers(head));
		    });

		    String sessionId = null;
		    BlackSession session = null;
		    if (req.requiresLogin()) {
			sessionId = request.headers("sessionid");
			final Bson filter = Filters.eq("sessionid", sessionId);
			final Document sessionInfo = collection.find(filter).first();
			if (sessionInfo != null) {
			    if (!(sessionInfo.containsKey("access_token") && sessionInfo.containsKey("refresh_token"))) {
				collection.deleteOne(filter);
				response.status(403);
				return new JSONObject().append("success", false).append("detailedReason", "Your Session has no Tokens.").toString();
			    } else {
				// everything good, the session is existing and even has tokens, great
				session = new BlackSession(sessionId);
			    }
			} else {
			    response.status(401);
			    return new JSONObject().append("success", false).append("detailedReason", "Not logged in.").toString();
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
			return new JSONObject().put("success", false).put("reason", 400).put("detailedReason", "jsonException").toString();
		    } else {
			response.status(500);
			return new JSONObject().put("success", false).put("reason", 500).put("detailedReason", "exception").toString();
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
