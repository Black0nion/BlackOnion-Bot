package com.github.black0nion.blackonionbot.config.generic;

public class ConfigSavingException extends ConfigException {

	public ConfigSavingException() {
		super();
	}

	public ConfigSavingException(String message) {
		super(message);
	}

	public ConfigSavingException(Throwable cause) {
		super(cause);
	}

	public ConfigSavingException(String message, Throwable cause) {
		super(message, cause);
	}
}
