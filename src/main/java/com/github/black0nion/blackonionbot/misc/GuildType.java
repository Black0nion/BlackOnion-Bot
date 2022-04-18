package com.github.black0nion.blackonionbot.misc;

import com.github.black0nion.blackonionbot.systems.settings.Setting;

public enum GuildType {
	/**
	 * Code: 0
	 */
	NORMAL(0, 3),
	/**
	 * Code: 1
	 */
	PREMIUM(1, 6),
	/**
	 * Code: 2
	 */
	BETA(2, 3),
	/**
	 * Code: 3
	 */
	SUPPORT_SERVER(3, Integer.MAX_VALUE);

	private final int code;
	private final int maxCustomCommands;

	GuildType(final int code, final int maxCustomCommands) {
		this.code = code;
		this.maxCustomCommands = maxCustomCommands;
	}

	public static GuildType parse(final String input) {
		try {
			return valueOf(input.toUpperCase());
		} catch (final Exception e) {
			return null;
		}
	}

	public int getCode() {
		return code;
	}

	/**
	 * @return the maxCustomCommands
	 */
	public int getMaxCustomCommands() {
		return maxCustomCommands;
	}

	/**
	 * @param type the GuildType to compare to
	 * @return if the current GuildType is higher in the hierarchy than the argument
	 */
	public boolean higherThan(final GuildType type) {
		return this.getCode() > type.getCode();
	}

	/**
	 * @param type the GuildType to compare to
	 * @return if the current GuildType is higher than or equal to the argument in the hierarchy
	 */
	public boolean higherThanOrEqual(final GuildType type) {
		return this.getCode() >= type.getCode();
	}
}