package com.github.black0nion.blackonionbot.rest.exception;

import io.javalin.http.HttpResponseException;
import io.javalin.http.HttpStatus;

import java.util.Map;

public class TooEarlyResponse extends HttpResponseException {

	private static final Map<String, String> EMPTY_MAP = Map.of();

	public TooEarlyResponse(String message) {
		super(HttpStatus.TOO_EARLY, message, EMPTY_MAP);
	}
}