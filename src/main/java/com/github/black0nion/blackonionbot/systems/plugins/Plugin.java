package com.github.black0nion.blackonionbot.systems.plugins;

import com.github.black0nion.blackonionbot.utils.Utils;

import java.lang.reflect.Method;
import java.util.Arrays;

public abstract class Plugin {

    private String name = this.getClass().getName();

	public abstract void onEnable();

    public void onDisable() {
	System.out.println("[" + this.getName() + "] Plugin disabled.");
    }

	public void onDisableNow() {
		System.out.println("[" + this.getName() + "] Plugin force disabling...");
	}

    public final String getName() {
	return this.name;
    }

    protected final void setName(final String name) {
	this.name = name;
    }

	private static final String[] methodNames = Arrays.stream(Plugin.class.getMethods()).map(Method::getName).toArray(String[]::new);

	public static boolean isMethod(String methodName) {
		return Utils.equalsOneIgnoreCase(methodName, methodNames);
	}
}