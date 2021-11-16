package com.github.black0nion.blackonionbot.systems.plugins;

public class PluginInformation {
	private final Plugin plugin;
	private PluginState state;
	private final Thread mainThread;
	private final ThreadGroup threadGroup;

	public PluginInformation(Plugin plugin, Thread mainThread, ThreadGroup threadGroup) {
		this.plugin = plugin;
		this.state = PluginState.STOPPED;
		this.mainThread = mainThread;
		this.threadGroup = threadGroup;
	}

	public Plugin getPlugin() {
		return plugin;
	}

	public PluginState getState() {
		return state;
	}

	public void setState(PluginState state) {
		this.state = state;
	}

	public Thread getMainThread() {
		return mainThread;
	}

	public ThreadGroup getThreadGroup() {
		return threadGroup;
	}
}