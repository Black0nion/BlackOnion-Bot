package com.github.black0nion.blackonionbot.misc;

/**
 * @author _SIM_
 */
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

    private GuildType(final int code) {
	this.code = code;
	this.maxCustomCommands = 3;
    }

    private GuildType(final int code, final int maxCustomCommands) {
	this.code = code;
	this.maxCustomCommands = maxCustomCommands;
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

    public static GuildType parse(final int input) {
        try {
            for (GuildType value : values()) {
                if (value.getCode() == input) return value;
            }
        } catch (final Exception ignored) {}
        return null;
    }
}