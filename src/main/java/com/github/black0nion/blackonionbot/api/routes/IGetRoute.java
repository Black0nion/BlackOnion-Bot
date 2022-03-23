package com.github.black0nion.blackonionbot.api.routes;

import com.github.black0nion.blackonionbot.utils.Time;

public interface IGetRoute extends IRoute {
    @Override
    default Time rateLimit() {
	return Time.MINUTES(40);
    }

    @Override
    default HttpMethod type() {
	return HttpMethod.GET;
    }
}