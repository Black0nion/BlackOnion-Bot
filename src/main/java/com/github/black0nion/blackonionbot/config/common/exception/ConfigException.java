package com.github.black0nion.blackonionbot.config.common.exception;

public class ConfigException extends RuntimeException {

	public ConfigException() {
		super();
	}

	public ConfigException(String message) {
		super(message);
	}

	public ConfigException(Throwable cause) {
		super(cause);
	}

	public ConfigException(String message, Throwable cause) {
		super(message, cause);
	}
}
