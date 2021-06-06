package com.github.black0nion.blackonionbot.misc;

/**
 * @author _SIM_
 */
public enum GuildType {
	/**
	 * Code: 0
	 */
	NORMAL(0),
	/**
	 * Code: 1
	 */
	PREMIUM(1),
	/**
	 * Code: 2
	 */
	BETA(2),
	/**
	 * Code: 3
	 */
	SUPPORT_SERVER(3);
	
	private final int code;
	
	private GuildType(final int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
	/**
	 * @param type the GuildType to compare to
	 * @return if the current GuildType is higher than the argument
	 */
	public boolean higherThan(final GuildType type) {
		return this.getCode() > type.getCode();
	}
	
	/**
	 * @param type the GuildType to compare to
	 * @return if the current GuildType is higher than or equal to the argument
	 */
	public boolean higherThanOrEqual(final GuildType type) {
		return this.getCode() >= type.getCode();
	}
	
	public static final GuildType parse(final String input) {
		try {
			return valueOf(input.toUpperCase());
		} catch (final Exception e) {
			return null;
		}
	}
}