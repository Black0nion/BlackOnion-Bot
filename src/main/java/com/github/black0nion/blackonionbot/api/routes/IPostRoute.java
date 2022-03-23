package com.github.black0nion.blackonionbot.api.routes;

import com.github.black0nion.blackonionbot.utils.Time;

public interface IPostRoute extends IRoute {

    @Override
    default boolean requiresLogin() {
		return true;
    }

    @Override
    default Time rateLimit() {
		return Time.MINUTES(20);
    }

    @Override
    default HttpMethod type() {
		return HttpMethod.POST;
    }
}