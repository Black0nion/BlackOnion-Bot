package com.github.black0nion.blackonionbot.config.discord.api.settings;

public class SettingSaveException extends RuntimeException {

	public SettingSaveException(String message) {
		super(message);
	}

	public SettingSaveException(Throwable cause) {
		super(cause);
	}

	public SettingSaveException(String message, Throwable cause) {
		super(message, cause);
	}
}
