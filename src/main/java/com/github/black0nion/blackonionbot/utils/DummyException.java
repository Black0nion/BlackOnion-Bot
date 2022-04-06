package com.github.black0nion.blackonionbot.utils;

/**
 * A dummy exception, useful for returning from a bottom level method
 */
public class DummyException extends RuntimeException {
	public DummyException() {
		super();
	}

	public DummyException(String message) {
		super(message);
	}
}
