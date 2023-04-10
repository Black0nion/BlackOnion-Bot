package com.github.black0nion.blackonionbot.misc.exception;

public class OAuthUserNotFoundException extends Exception {
	public OAuthUserNotFoundException() {
		super("User not found in database");
	}

	public OAuthUserNotFoundException(String message) {
		super(message);
	}

	public OAuthUserNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public OAuthUserNotFoundException(Throwable cause) {
		super(cause);
	}
}