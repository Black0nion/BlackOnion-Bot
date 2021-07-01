/**
 * Date: 01.07.2021
 * Project: BlackOnion-Bot
 *
 * @author _SIM_
 */
package com.github.black0nion.blackonionbot.systems.plugins;

/**
 * @author _SIM_
 */
public abstract class Plugin {

    private String name = this.getClass().getName();

    public abstract void onEnable();

    public void onDisable() {
	System.out.println("[" + this.getName() + "] Plugin disabled.");
    }

    /**
     * @return the name
     */
    public final String getName() {
	return this.name;
    }

    /**
     * @param name the name to set
     */
    protected final void setName(final String name) {
	this.name = name;
    }
}