package com.github.black0nion.blackonionbot.inject;

public class InjectorCreateInstanceException extends RuntimeException {

	public InjectorCreateInstanceException(String message) {
		super(message);
	}

	public InjectorCreateInstanceException(String message, Throwable cause) {
		super(message, cause);
	}

	public InjectorCreateInstanceException(Throwable t) {
		super(t);
	}
}
