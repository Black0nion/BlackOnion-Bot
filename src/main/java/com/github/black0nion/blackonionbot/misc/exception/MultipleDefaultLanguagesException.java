package com.github.black0nion.blackonionbot.misc.exception;

public class MultipleDefaultLanguagesException extends RuntimeException {

	public static final String MESSAGE = "There can only be one default language!";

	public MultipleDefaultLanguagesException() {
		super(MESSAGE);
	}
}
