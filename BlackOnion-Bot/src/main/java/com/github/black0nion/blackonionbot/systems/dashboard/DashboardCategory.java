/**
 *
 */
package com.github.black0nion.blackonionbot.systems.dashboard;

/**
 * @author _SIM_
 */
public enum DashboardCategory {
    GENERAL("General", "general"), MODERATION("Moderation", "moderation");

    private final String id;
    private final String name;

    private DashboardCategory(final String name, final String id) {
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