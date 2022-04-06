package com.github.black0nion.blackonionbot.misc;

import javax.annotation.Nullable;

public enum Category {
	OTHER, MODERATION, FUN, BOT, MUSIC, MISC, INFORMATION, ADMIN;

	/**
	 * @return the Category or null
	 */
	@Nullable
	public static Category parse(final String input) {
		try {
			return valueOf(input.toUpperCase());
		} catch (final Exception e) {
			return null;
		}
	}
}
