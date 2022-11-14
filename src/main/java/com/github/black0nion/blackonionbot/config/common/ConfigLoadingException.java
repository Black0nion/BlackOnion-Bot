package com.github.black0nion.blackonionbot.config.common;

public class ConfigLoadingException extends ConfigException {

	public ConfigLoadingException() {
		super();
	}

	public ConfigLoadingException(String message) {
		super(message);
	}

	public ConfigLoadingException(Throwable cause) {
		super(cause);
	}

	public ConfigLoadingException(String message, Throwable cause) {
		super(message, cause);
	}
}
