package com.github.black0nion.blackonionbot.systems.plugins;

public abstract class Plugin {

	private final String name;

	public Plugin() {
		this.name = this.getClass().getSimpleName();
	}

	public Plugin(String name) {
		this.name = name;
	}

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
}
