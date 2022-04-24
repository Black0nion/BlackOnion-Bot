package com.github.black0nion.blackonionbot.systems.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Plugin {
	private static final Logger logger = LoggerFactory.getLogger(Plugin.class);

	private final String name;

	public Plugin() {
		this.name = this.getClass().getSimpleName();
	}

	public Plugin(String name) {
		this.name = name;
	}

	public abstract void onEnable();

	public void onDisable() {
		logger.info("['{}'] Plugin disabled.", this.getName());
	}

	public void onDisableNow() {
		logger.info("['{}'] Plugin force disabling...", this.getName());
	}

	public final String getName() {
		return this.name;
	}
}