package com.github.black0nion.blackonionbot.API;

import com.github.black0nion.blackonionbot.utils.Time;

public abstract class GetRequest extends BlackRequest {
    @Override
    public abstract String url();

    @Override
    public Time rateLimit() {
	return Time.MINUTES(20);
    }

    @Override
    public RequestType type() {
	return RequestType.GET;
    }
}