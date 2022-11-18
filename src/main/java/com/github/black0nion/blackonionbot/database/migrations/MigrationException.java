package com.github.black0nion.blackonionbot.database.migrations;

public class MigrationException extends RuntimeException {

	public MigrationException() {
	}

	public MigrationException(String s) {
		super(s);
	}

	public MigrationException(String message, Throwable cause) {
		super(message, cause);
	}

	public MigrationException(Throwable cause) {
		super(cause);
	}

	public MigrationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
