package com.github.black0nion.blackonionbot.api.routes;

import com.github.black0nion.blackonionbot.utils.Time;
import io.javalin.http.BadRequestResponse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IRoute {
	@Nonnull
	String url();

	/**
	 * @return The {@link Time#time() amount} of valid requests per {@link Time#unit() unit}.
	 */
	@Nullable
	default Time rateLimit() {
		return null;
	}

	default String assertMatches(String input, String regex) {
		return assertMatches(input, "Input", regex);
	}

	default String assertMatches(String input, String name, String regex) {
		if (!input.matches(regex))
			throw new BadRequestResponse(name + " does not match regex: " + regex);
		return input;
	}
}