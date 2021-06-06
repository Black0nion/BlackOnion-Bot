package com.github.black0nion.blackonionbot.misc;

public enum Category {
	OTHER, MODERATION, FUN, BOT, MUSIC, MISC, INFORMATION;

	/**
	 * @param string
	 * @return
	 */
	public static final Category parse(final String input) {
		try {
			return valueOf(input.toUpperCase());
		} catch (final Exception e) {			
			return null;
		}
	}
}