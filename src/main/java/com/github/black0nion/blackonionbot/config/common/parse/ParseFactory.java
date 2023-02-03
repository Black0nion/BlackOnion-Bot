package com.github.black0nion.blackonionbot.config.common.parse;

import com.github.black0nion.blackonionbot.config.common.exception.ParseException;

public interface ParseFactory<I, O> {

	Class<I> getInputClass();

	/**
	 * Parses the input and returns the parsed value.
	 * Only throws {@link ParseException} if the input is invalid, no other exception can be thrown.
	 * @throws ParseException if the input is invalid
	 */
	default O parse(I input) {
		if (input == null) {
			if (allowsNull()) return null;

			throw new ParseException("Null input is not allowed");
		}
		try {
			return parseImpl(input);
		} catch (ParseException e) {
			throw e;
		} catch (Exception e) {
			throw new ParseException("Failed to parse input: " + input, e);
		}
	}

	O parseImpl(I input) throws Exception;

	/**
	 * Opt-in to allow null values to be parsed.
	 */
	default boolean allowsNull() {
		return false;
	}
}
