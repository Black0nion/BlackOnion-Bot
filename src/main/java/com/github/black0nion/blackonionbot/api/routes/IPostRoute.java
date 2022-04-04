package com.github.black0nion.blackonionbot.api.routes;

import com.github.black0nion.blackonionbot.utils.Time;
import io.javalin.http.HandlerType;

import javax.annotation.Nonnull;

public interface IPostRoute extends IHttpRoute {

    @Override
    default boolean requiresLogin() {
		return true;
    }

    @Override
    default Time rateLimit() {
		return Time.MINUTES(20);
    }

    @Override
    default @Nonnull HandlerType type() {
		return HandlerType.POST;
    }
}