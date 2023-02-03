package com.github.black0nion.blackonionbot.config.common.exception;

public class ParseException extends RuntimeException {
	public ParseException(String message) {
		super(message);
	}

	public ParseException(Throwable cause) {
		super(cause);
	}

	public ParseException(String message, Throwable cause) {
		super(message, cause);
	}
}
