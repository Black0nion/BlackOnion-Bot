package com.github.black0nion.blackonionbot.misc.exception;

public class MultipleDefaultLanguagesException extends RuntimeException {

	public MultipleDefaultLanguagesException() {
	}

	public MultipleDefaultLanguagesException(String message) {
		super(message);
	}

	public MultipleDefaultLanguagesException(String message, Throwable cause) {
		super(message, cause);
	}

	public MultipleDefaultLanguagesException(Throwable cause) {
		super(cause);
	}

	public MultipleDefaultLanguagesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
