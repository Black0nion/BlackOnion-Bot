package com.github.black0nion.blackonionbot.api;

import com.beust.jcommander.internal.Lists;
import com.github.black0nion.blackonionbot.api.impl.get.Paths;
import com.github.black0nion.blackonionbot.api.routes.IHttpRoute;
import com.github.black0nion.blackonionbot.api.routes.IWebSocketEndpoint;
import com.github.black0nion.blackonionbot.api.sessions.GenericSession;
import com.github.black0nion.blackonionbot.api.sessions.RestSession;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.utils.DummyException;
import com.github.black0nion.blackonionbot.stats.JettyCollector;
import com.github.black0nion.blackonionbot.utils.Time;
import com.github.black0nion.blackonionbot.utils.config.Config;
import io.javalin.Javalin;
import io.javalin.http.*;
import io.javalin.http.util.JsonEscapeUtil;
import io.javalin.http.util.NaiveRateLimit;
import io.javalin.jetty.JettyUtil;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.json.JSONObject;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.*;

public class API {
	private static final Logger logger = LoggerFactory.getLogger(API.class);
	private static final List<IHttpRoute> httpRoutes = new ArrayList<>();
	private static Javalin app;

	public static Javalin getApp() {
		return app;
	}

	@Reloadable("api")
	public static void init() {
		startApi(Config.getInstance().getApiPort() > 0 ? Config.getInstance().getApiPort() : 187);
	}

	static void startApi(int port) {
		if (app != null) {
			app.close();
		}

		StatisticsHandler statisticsHandler = new StatisticsHandler();
		JettyCollector.initialize(statisticsHandler);
		app = Javalin.create(config -> {
			config.registerPlugin(new Paths.PathListener());
			config.enableCorsForAllOrigins();
			config.server(() -> {
				// forcefully get default server (the same as Javalin.create() does)
				Server server = JettyUtil.getOrDefault(null);
				server.setHandler(statisticsHandler);
				return server;
			});
		}).start(port);

		final Reflections reflections = new Reflections(API.class.getPackage().getName());

		//region Map WebSockets
		final Set<Class<? extends IWebSocketEndpoint>> websocketsClasses = reflections.getSubTypesOf(IWebSocketEndpoint.class);

		for (final Class<? extends IWebSocketEndpoint> websockets : websocketsClasses) {
			try {
				IWebSocketEndpoint endpoint = websockets.getConstructor().newInstance();
				app.ws("/ws/" + endpoint.url(), ws -> {
					ws.onConnect(ctx -> endpoint.onConnect(ctx.session));
					ws.onMessage(ctx -> endpoint.onMessage(ctx.session, ctx.message()));
					ws.onClose(ctx -> endpoint.onClose(ctx.session, ctx.status(), ctx.reason()));
					ws.onError(ctx -> endpoint.onError(ctx.session, ctx.error()));
				});
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		//endregion

		//region Map Requests
		final Set<Class<? extends IHttpRoute>> requestClasses = reflections.getSubTypesOf(IHttpRoute.class);

		for (final Class<?> req : requestClasses) {
			try {
				// filter out interfaces like IPostRoute and IGetRoute
				if (IHttpRoute.class.isAssignableFrom(req) && !req.isInterface()) {
					httpRoutes.add((IHttpRoute) req.getConstructor().newInstance());
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		ExceptionHandler<Exception> exceptionHandler = (e, ctx) -> {
			// dummy exceptions are just to instantly return from a handler, we don't care about them
			if (e instanceof DummyException) return;

			// only log unexpected exceptions
			if (!(e instanceof HttpResponseException))
				logger.error("API Error happened", e);

			@Nullable final HttpResponseException http = e instanceof HttpResponseException eAsHttp ? eAsHttp : null;
			ctx.status(http != null ? http.getStatus() : 500).result(
                "{" +
                "\n   \"message\": \"" + JsonEscapeUtil.INSTANCE.escape(e.getMessage()) + "\"," +
                "\n   \"status\": " + (http != null ? http.getStatus() : 500) +
                "\n}").contentType(ContentType.APPLICATION_JSON);

			if (ctx.status() == 429) {
				logger.warn("IP {} exceeded rate limit for {} which is {}", ctx.ip(), ctx.path(),
					// oh my god why am I introducing such code
					http != null && http.getMessage() != null ? http.getMessage().replaceFirst("Rate limit exceeded - Server allows ", "").replace(".", "") : "unknown"
				);
			}
		};
		app.exception(Exception.class, exceptionHandler);
		app.exception(HttpResponseException.class, exceptionHandler);

		app.before(ctx -> {
			final String ip = ctx.header("X-Real-IP") != null ? ctx.header("X-Real-IP") : ctx.ip();
			logger.info("{} Request from IP {} > {}", ctx.method(), ip, ctx.fullUrl());
		});

		for (final IHttpRoute req : httpRoutes) {
			final String url = "/api/" + req.url();
			final Handler handler = ctx -> {
				Time rateLimit = req.rateLimit();
				if (rateLimit != null)
					NaiveRateLimit.requestPerTimeUnit(ctx, rateLimit.time(), rateLimit.unit());

				if (req.isJson())
					ctx.contentType(ContentType.APPLICATION_JSON);

				//region Headers
				final Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
				headers.putAll(ctx.headerMap());

				List<String> requiredHeaders = Lists.newArrayList(req.requiredHeaders());
				if (req.requiresLogin()) requiredHeaders.add("sessionid");

				if (!headers.keySet().containsAll(requiredHeaders)) {
					requiredHeaders.removeAll(headers.keySet());
					throw new BadRequestResponse("Missing headers: " + requiredHeaders);
				}
				//endregion

				//region Body
				JSONObject body = new JSONObject();
				if (req.requiredBodyParameters().length != 0) {
					body = new JSONObject(ctx.body());
				}

				for (final String parameter : req.requiredBodyParameters()) {
					if (!body.has(parameter)) {
						throw new BadRequestResponse("Missing parameter in body: " + parameter);
					}
				}
				//endregion

				//region Session
				RestSession session = null;
				if (req.requiresLogin()) {
					final String sessionId = ctx.header("sessionid");
					if (sessionId == null) throw new BadRequestResponse("No SessionID provided");
					if (!sessionId.matches(GenericSession.SESSIONID_REGEX)) throw new BadRequestResponse("Invalid SessionID");
					try {
						session = new RestSession(sessionId);
					} catch (Exception e) {
						if (e instanceof InputMismatchException) {
							throw new UnauthorizedResponse("Unknown Session");
						} else if (e instanceof NullPointerException) {
							logger.error("Session ID is null, shouldn't happen because of the filter before!");
						}
						throw e;
					}

					if (session.getUser() == null) {
						throw new UnauthorizedResponse("No user found");
					}
				}
				//endregion

				// Objects.toString to make it null safe
				Object result = req.handle(ctx, body, headers, session, session != null ? session.getUser() : null);
				if (result instanceof JSONObject json) {
					ctx.contentType(ContentType.APPLICATION_JSON);
					result = json.toString();
				}
				ctx.result(Objects.toString(result));
			};

			app.addHandler(req.type(), url, handler);
		}
		logger.info("Started API server!");
	}
}