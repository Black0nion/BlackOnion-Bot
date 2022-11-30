package com.github.black0nion.blackonionbot.misc.exception;

/**
 * This <b>unchecked</b> exception is used to wrap the cause of another exception.
 */
public class CauseWrapperException extends RuntimeException {
	public CauseWrapperException(Throwable cause) {
		super(cause);
	}
}
