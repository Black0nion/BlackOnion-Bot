package com.github.black0nion.blackonionbot.misc.exception;

public class SQLSetupException extends RuntimeException {

	public SQLSetupException() {}

	public SQLSetupException(String message) {
		super(message);
	}

	public SQLSetupException(String message, Throwable cause) {
		super(message, cause);
	}

	public SQLSetupException(Throwable cause) {
		super(cause);
	}

	public SQLSetupException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
