package com.github.black0nion.blackonionbot.misc.exception;

public class LanguageCreationException extends RuntimeException {

	public LanguageCreationException(String code, String name, Exception e) {
		super("Could not create language " + name + " (" + code + ")", e);
	}
}
