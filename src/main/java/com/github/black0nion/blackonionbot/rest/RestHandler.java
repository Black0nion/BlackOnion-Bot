package com.github.black0nion.blackonionbot.rest;

import com.beust.jcommander.internal.Lists;
import com.github.black0nion.blackonionbot.rest.api.IHttpRoute;
import com.github.black0nion.blackonionbot.rest.sessions.AbstractSession;
import com.github.black0nion.blackonionbot.rest.sessions.RestSession;
import com.github.black0nion.blackonionbot.utils.Time;
import io.javalin.http.*;
import io.javalin.http.util.NaiveRateLimit;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.*;

public class RestHandler implements Handler {

	private final IHttpRoute route;

	public RestHandler(IHttpRoute route) {
		this.route = route;
	}

	@Override
	public void handle(@Nonnull Context ctx) throws Exception {
		Time rateLimit = route.rateLimit();
		if (rateLimit != null)
			NaiveRateLimit.requestPerTimeUnit(ctx, rateLimit.time(), rateLimit.unit());

		if (route.isJson())
			ctx.contentType(ContentType.APPLICATION_JSON);

		//region Headers
		final Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		headers.putAll(ctx.headerMap());

		List<String> requiredHeaders = Lists.newArrayList(route.requiredHeaders());
		if (route.requiresLogin()) requiredHeaders.add("sessionid");

		if (!headers.keySet().containsAll(requiredHeaders)) {
			requiredHeaders.removeAll(headers.keySet());
			throw new BadRequestResponse("Missing headers: " + requiredHeaders);
		}
		//endregion

		//region Body
		JSONObject body = new JSONObject();
		if (route.requiredBodyParameters().length != 0) {
			body = new JSONObject(ctx.body());
		}

		for (final String parameter : route.requiredBodyParameters()) {
			if (!body.has(parameter)) {
				throw new BadRequestResponse("Missing parameter in body: " + parameter);
			}
		}
		//endregion

		//region Session
		RestSession session = null;
		if (route.requiresLogin()) {
			final String sessionId = ctx.header("sessionid");
			if (sessionId == null) throw new BadRequestResponse("No SessionID provided");
			if (!sessionId.matches(AbstractSession.SESSIONID_REGEX)) throw new BadRequestResponse("Invalid SessionID");
			try {
				session = new RestSession(sessionId);
			} catch (InputMismatchException e) {
				throw new UnauthorizedResponse("Unknown Session");
			} catch (NullPointerException e) {
				// not a field because it's incredibly unlikely to happen
				LoggerFactory.getLogger(API.class).error("Session ID is null, shouldn't happen because of the filter before!");
				throw e;
			}

			if (session.getUser() == null) {
				throw new UnauthorizedResponse("No user found");
			}
		}
		//endregion

		Object result = route.handle(ctx, body, session, session != null ? session.getUser() : null);
		if (result == null) {
			if (ctx.status() == HttpStatus.OK)
				ctx.status(HttpStatus.NO_CONTENT);
			return;
		} else if (result instanceof JSONObject json) {
			ctx.contentType(ContentType.APPLICATION_JSON);
			result = json.toString();
		}
		ctx.result(Objects.toString(result));
	}
}
