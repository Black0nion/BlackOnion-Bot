package com.github.black0nion.blackonionbot.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class BlackThread extends Thread {

	public BlackThread(Runnable runnable) {
		super(runnable);
	}

	public BlackThread(Runnable runnable, String threadName) {
		super(runnable, threadName);
	}

	public BlackThread(@NotNull String threadName) {
		super(threadName);
	}

	public BlackThread(@Nullable ThreadGroup group, Runnable runnable) {
		super(group, runnable);
	}

	public BlackThread(@Nullable ThreadGroup group, Runnable runnable, @NotNull String threadName, long stack) {
		super(group, runnable, threadName, stack);
	}

	public BlackThread(@Nullable ThreadGroup group, Runnable runnable, @NotNull String threadName) {
		super(group, runnable, threadName);
	}

	public BlackThread(@Nullable ThreadGroup group, @NotNull String threadName) {
		super(group, threadName);
	}

	public BlackThread setThreadName(String name) {
		super.setName(name);
		return this;
	}

	public synchronized BlackThread startThread() {
		super.start();
		return this;
	}
}
