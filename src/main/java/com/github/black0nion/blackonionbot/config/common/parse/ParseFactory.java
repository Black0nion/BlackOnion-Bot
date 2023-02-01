package com.github.black0nion.blackonionbot.config.common.parse;

import com.github.black0nion.blackonionbot.config.common.exception.ParseException;

public interface ParseFactory<T> {

	/**
	 * Parses the input and returns the parsed value.
	 * Only throws {@link ParseException} if the input is invalid, no other exception can be thrown.
	 * @throws ParseException if the input is invalid
	 */
	default T parse(String input) {
		try {
			return parseImpl(input);
		} catch (ParseException e) {
			throw e;
		} catch (Exception e) {
			throw new ParseException("Failed to parse input: " + input, e);
		}
	}

	T parseImpl(String input) throws Exception;

	/**
	 * Opt-in to allow null values to be parsed.
	 */
	default boolean allowsNull() {
		return false;
	}
}
