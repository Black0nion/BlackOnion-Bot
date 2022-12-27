package com.github.black0nion.blackonionbot.utils;

/**
 * A dummy exception, useful for returning from a bottom level method
 */
public class CommandReturnException extends RuntimeException {
	public CommandReturnException() {
		super();
	}

	public CommandReturnException(String message) {
		super(message);
	}
}
