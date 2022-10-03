package com.github.black0nion.blackonionbot.rest.api;

import com.github.black0nion.blackonionbot.utils.Time;
import io.javalin.http.HandlerType;

import javax.annotation.Nonnull;

public interface IGetRoute extends IHttpRoute {
    @Override
    default Time rateLimit() {
	return Time.MINUTES(40);
    }

    @Override
    default @Nonnull HandlerType type() {
	return HandlerType.GET;
    }
}
