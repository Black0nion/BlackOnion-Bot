/**
 *
 */
package com.github.black0nion.blackonionbot.systems.dashboard.sections;

/**
 * @author _SIM_
 */
public enum Category {
    GENERAL("General", "general"), MODERATION("Moderation", "moderation");

    private final String id;
    private final String name;

    private Category(final String name, final String id) {
	this.name = name;
	this.id = id;
    }

    /**
     * @return the id
     */
    public String getId() {
	return this.id;
    }

    /**
     * @return the pretty name
     */
    public String getName() {
	return this.name;
    }
}