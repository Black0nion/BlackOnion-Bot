/**
 * Date: 01.07.2021
 * Project: BlackOnion-Bot
 *
 * @author _SIM_
 */
package com.github.black0nion.blackonionbot.systems.plugins;

import com.github.black0nion.blackonionbot.utils.Utils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author _SIM_
 */
public abstract class Plugin {

    private String name = this.getClass().getName();

	public abstract void onEnable();

    public void onDisable() {
	System.out.println("[" + this.getName() + "] Plugin disabled.");
    }

	public void onDisableNow() {}

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

	private static final String[] methodNames = Arrays.stream(Plugin.class.getMethods()).map(Method::getName).toArray(String[]::new);

	public static boolean isMethod(String methodName) {
		return Utils.equalsOneIgnoreCase(methodName, methodNames);
	}
}