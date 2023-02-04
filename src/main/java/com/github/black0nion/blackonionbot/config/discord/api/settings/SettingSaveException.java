package com.github.black0nion.blackonionbot.config.discord.api.settings;

import java.util.Objects;

public class SettingSaveException extends RuntimeException {

	public SettingSaveException(String message) {
		super(message);
	}

	public SettingSaveException(Throwable cause) {
		super(cause);
	}

	public SettingSaveException(Setting<?> setting, String message) {
		super(setting + ": " + message);
	}

	public SettingSaveException(Setting<?> setting, Throwable cause) {
		super(Objects.toString(setting), cause);
	}

	public SettingSaveException(String message, Throwable cause) {
		super(message, cause);
	}
}
