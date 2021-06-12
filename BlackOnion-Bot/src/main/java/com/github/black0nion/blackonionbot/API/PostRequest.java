package com.github.black0nion.blackonionbot.API;

import com.github.black0nion.blackonionbot.utils.Time;

public abstract class PostRequest extends BlackRequest {

    @Override
    public boolean requiresLogin() {
	return true;
    }

    @Override
    public abstract String url();

    @Override
    public Time rateLimit() {
	return Time.MINUTES(10);
    }

    @Override
    public RequestType type() {
	return RequestType.POST;
    }
}