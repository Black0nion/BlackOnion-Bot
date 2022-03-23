package com.github.black0nion.blackonionbot.api;

import com.beust.jcommander.internal.Lists;
import com.github.black0nion.blackonionbot.api.routes.IRoute;
import com.github.black0nion.blackonionbot.api.routes.IWebSocketEndpoint;
import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.utils.BlackRateLimiter;
import com.github.black0nion.blackonionbot.utils.config.Config;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONException;
import org.json.JSONObject;
import org.reflections.Reflections;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.util.*;

public class API {

	private static final HashMap<String, IRoute> requests = new HashMap<>();
	private static final List<String> websocketEndpoints = new ArrayList<>();

	private static final HashMap<String, BlackRateLimiter> rateLimiters = new HashMap<>();

	private static final MongoCollection<Document> collection = MongoDB.DATABASE.getCollection("dashboard-sessions");

	@Reloadable("ratelimits")
	public static void clearRateLimits() {
		rateLimiters.clear();
	}

	@Reloadable("api")
	public static void init() {
		requests.clear();
		websocketEndpoints.clear();
		clearRateLimits();

		Spark.stop();
		Spark.awaitStop();

		Spark.port(Config.api_port > 0 ? Config.api_port : 187);

		final Reflections reflections = new Reflections(API.class.getPackage().getName());

		//region Map WebSockets
		final Set<Class<? extends IWebSocketEndpoint>> websocketsClasses = reflections.getSubTypesOf(IWebSocketEndpoint.class);

		for (final Class<? extends IWebSocketEndpoint> websockets : websocketsClasses) {
			IWebSocketEndpoint endpoint;
			try {
				endpoint = websockets.getConstructor().newInstance();
				Spark.webSocket("/ws/" + endpoint.getRoute(), websockets);
				websocketEndpoints.add("/ws/" + endpoint.getRoute());
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		//endregion

		Spark.init();

		//region Map Requests
		final Set<Class<? extends IRoute>> requestClasses = reflections.getSubTypesOf(IRoute.class);

		for (final Class<?> req : requestClasses) {
			try {
				if (IRoute.class.isAssignableFrom(req) && !req.isInterface()) {
					final IRoute newInstance = (IRoute) req.getConstructor().newInstance();
					requests.put("/api/" + newInstance.url(), newInstance);
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		//endregion

		// Error handling
		Spark.internalServerError((request, response) -> {
			response.header("Access-Control-Allow-Origin", "*");
			response.type("application/json");
			logError("Some internal server error happened! URL: " + request.url());
			return exception("Internal server error", 500, response);
		});

		final String ratelimited = new JSONObject().put("error", "ratelimited").toString();
		Spark.before((req, res) -> {
			final String ip = req.headers("X-Real-IP") != null ? req.headers("X-Real-IP") : req.ip();
			logInfo("IP " + ip + " tried to connect to " + req.url());
			if (!(!requests.containsKey(req.uri()) && !websocketEndpoints.contains(req.uri()))) {
				// TODO: ratelimit for websockets
				final IRoute request = requests.get(req.uri());
				// null if it's a websocket endpoint
				if (request == null) return;
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

		for (final IRoute req : requests.values()) {
			if (req.url() == null) {
				logError("Path is null: " + req.getClass().getName());
				continue;
			}

			final String url = "/api/" + req.url();
			final Route route = (request, response) -> {
				final String ip = request.headers("X-Real-IP") != null ? request.headers("X-Real-IP") : request.ip();
				try {
					response.header("Access-Control-Allow-Origin", "*");

					//region Headers
					final HashMap<String, String> headers = new HashMap<>();
					request.headers().forEach(head -> headers.put(head.toLowerCase(), request.headers(head)));

					if (!headers.keySet().containsAll(Arrays.asList(req.requiredParameters()))) {
						List<String> requiredParams = Lists.newArrayList(req.requiredParameters());
						requiredParams.removeAll(headers.keySet());
						return exception("Parameters missing: " + requiredParams, 400, response);
					}
					//endregion

					//region Body
					JSONObject body = new JSONObject();
					if (req.requiredBodyParameters().length != 0) {
						body = new JSONObject(request.body());
					}

					for (final String parameter : req.requiredBodyParameters()) {
						if (!body.has(parameter)) {
							return exception("Parameter missing: " + parameter, 400, response);
						}
					}
					//endregion

					//region Session
					BlackSession session = null;
					if (req.requiresLogin()) {
						final String sessionId = request.headers("sessionid");
						if (sessionId == null) return exception("No sessionid provided!", 401, response);
						final Bson filter = Filters.eq("sessionid", sessionId);
						final Document sessionInfo = collection.find(filter).first();
						if (sessionInfo != null) {
							if (!(sessionInfo.containsKey("access_token") && sessionInfo.containsKey("refresh_token"))) {
								collection.deleteOne(filter);
								return exception("Session has no tokens!", 401, response);
							} else {
								// everything good, the session is existing and even has tokens, great
								try {
									session = new BlackSession(sessionId);
								} catch (Exception e) {
									if (e instanceof InputMismatchException) {
										return exception("Session is invalid!", 401, response);
									} else if (e instanceof NullPointerException) {
										logError("Session ID is null, shouldn't happen because of the filter before!");
										return exception(e, response);
									} else {
										logError("Unknown exception: " + e.getMessage());
										return exception(e, response);
									}
								}
							}
						} else {
							return exception("No session info found!", 401, response);
						}

						if (session.getUser() == null) {
							return exception("Not logged in!", 401, response);
						}
					}
					//endregion

					return req.handle(request, response, body, headers, session, session != null ? session.getUser() : null);
				} catch (final Exception e) {
					response.type("application/json");
					if (e instanceof JSONException) {
						return exception("Invalid JSON!", 400, response);
					} else {
						logInfo("Error in api happened! Path: " + url + " from: " + ip);
						e.printStackTrace();
						return exception("Internal server error!", 500, response);
					}
				}
			};

			switch (req.type()) {
				case GET -> Spark.get(url, route);
				case POST -> Spark.post(url, route);
				case PUT -> Spark.put(url, route);
				case PATCH -> Spark.patch(url, route);
				case DELETE -> Spark.delete(url, route);
				case HEAD -> Spark.head(url, route);
				case TRACE -> Spark.trace(url, route);
				case CONNECT -> Spark.connect(url, route);
				case OPTIONS -> Spark.options(url, route);
				default -> logError("Unknown type: " + req.type());
			}
		}

		final spark.Route notFoundRoute = (request, response) -> {
			if (websocketEndpoints.contains(request.uri()) && request.headers("upgrade") != null) return null;
			response.header("Access-Control-Allow-Origin", "*");
			return exception("Not found!", 404, response);
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

	public static String exception(Throwable e) {
		return "{\"error\":\"" + e.getMessage() + "\"}";
	}

	public static String exception(Throwable e, Response response) {
		response.status(500);
		response.type("application/json");
		return exception(e);
	}

	public static String exception(String text) {
		return "{\"error\":\"" + text + "\"}";
	}

	public static String exception(String text, Response response) {
		response.status(500);
		response.type("application/json");
		return exception(text);
	}

	public static String exception(String text, int code, Response response) {
		response.status(code);
		response.type("application/json");
		return "{\"error\":\"" + text + "\"}";
	}
}