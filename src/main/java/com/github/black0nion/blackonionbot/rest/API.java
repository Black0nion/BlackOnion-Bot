package com.github.black0nion.blackonionbot.rest;

import com.github.black0nion.blackonionbot.config.api.Config;
import com.github.black0nion.blackonionbot.inject.Injector;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.rest.api.IHttpRoute;
import com.github.black0nion.blackonionbot.rest.api.IWebSocketEndpoint;
import com.github.black0nion.blackonionbot.rest.impl.get.Paths;
import com.github.black0nion.blackonionbot.stats.StatsCollectorFactory;
import com.github.black0nion.blackonionbot.utils.DummyException;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.http.ExceptionHandler;
import io.javalin.http.HttpResponseException;
import io.javalin.http.util.JsonEscapeUtil;
import io.javalin.jetty.JettyUtil;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class API {
	private static final Logger logger = LoggerFactory.getLogger(API.class);
	private final List<IHttpRoute> httpRoutes = new ArrayList<>();
	private Javalin app;

	public Javalin getApp() {
		return app;
	}

	private static API instance;

	public static API getInstance() {
		return instance;
	}

	@Reloadable("api")
	private static void reload() {
		new API(instance.config, instance.injector, instance.statsCollectorFactory);
	}

	private final Config config;
	private final Injector injector;
	private final StatsCollectorFactory statsCollectorFactory;

	public API(Config config, Injector injector, StatsCollectorFactory statsCollectorFactory) {
		instance = this;
		this.config = config;
		this.injector = injector;
		this.statsCollectorFactory = statsCollectorFactory;
		start();
	}

	private static final StatisticsHandler STATISTICS_HANDLER = new StatisticsHandler();

	/**
	 * Initializes the stats collector
	 * Will only actually init the underlying collector once per application start
	 */
	private void initStats() {
		statsCollectorFactory.init(STATISTICS_HANDLER);
	}

	private void start() {
		if (app != null) {
			app.close();
		}

		initStats();

		app = Javalin.create(cfg -> {
			cfg.registerPlugin(new Paths.PathListener());
			cfg.enableCorsForAllOrigins();
			cfg.server(() -> {
				// forcefully get default server (the same as Javalin.create() does)
				Server server = JettyUtil.getOrDefault(null);
				server.setHandler(STATISTICS_HANDLER);
				return server;
			});
		}).start(config.getApiPort() > 0 ? config.getApiPort() : 8080);

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

		httpRoutes.clear();
		for (final Class<?> req : requestClasses) {
			try {
				// filter out interfaces like IPostRoute and IGetRoute
				if (IHttpRoute.class.isAssignableFrom(req) && !req.isInterface()) {
					IHttpRoute route = createRoute(req);
					if (route != null)
						httpRoutes.add(route);
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

			app.addHandler(req.type(), url, new RestHandler(req));
		}
		logger.info("Started API server!");
	}

	private IHttpRoute createRoute(Class<?> req) {
		return injector.createInstance(req, IHttpRoute.class);
	}
}