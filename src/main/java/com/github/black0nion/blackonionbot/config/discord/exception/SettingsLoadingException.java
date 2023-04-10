package com.github.black0nion.blackonionbot.config.discord.exception;

public class SettingsLoadingException extends RuntimeException {

	public SettingsLoadingException() {
		super();
	}

	public SettingsLoadingException(String message) {
		super(message);
	}

	public SettingsLoadingException(Throwable cause) {
		super(cause);
	}

	public SettingsLoadingException(String message, Throwable cause) {
		super(message, cause);
	}

}
