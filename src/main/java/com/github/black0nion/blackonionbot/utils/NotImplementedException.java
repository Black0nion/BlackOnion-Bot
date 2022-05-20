package com.github.black0nion.blackonionbot.utils;

@SuppressWarnings("unused")
public class NotImplementedException extends RuntimeException {
	public NotImplementedException() {
		super("Not implemented!");
	}

	public NotImplementedException(Object name) {
		this(name.toString());
	}

	/**
	 * @param name The name of the method that was not implemented. Sets the message to "Not implemented: {@code name}"
	 */
	public NotImplementedException(String name) {
		super("Not implemented: " + name);
	}
}