package com.github.black0nion.blackonionbot.systems.settings;

public interface ConsumerCancellable<T> {
	boolean cancelled = false;

	default boolean isCancelled() {
		return cancelled;
	}

	void accept(T t);
}