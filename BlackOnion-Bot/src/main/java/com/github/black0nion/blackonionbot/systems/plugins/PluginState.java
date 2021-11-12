package com.github.black0nion.blackonionbot.systems.plugins;

public enum PluginState {
	NOT_STARTED_YET,
	/**
	 * The plugin is loaded and ready to be used
	 */
	RUNNING,
	/**
	 * The plugin is being disabled
	 */
	DISABLING,
	/**
	 * The plugin is being warned to disable
	 */
	DISABLING_NOW,
	/**
	 * The plugin threw an exception or was stopped with an unnormal status code
	 */
	ERRORED,
	/**
	 * The plugin stopped normally
	 */
	STOPPED,
	/**
	 * The plugin got forcefully shut down
	 */
	TERMINATED;
}