package com.github.black0nion.blackonionbot.inject;

public class InjectorCreateInstanceException extends RuntimeException {

	public InjectorCreateInstanceException(Exception e) {
		super(e);
	}

	public InjectorCreateInstanceException(Throwable t) {
		super(t);
	}
}
