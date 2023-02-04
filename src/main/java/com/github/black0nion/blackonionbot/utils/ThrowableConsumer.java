package com.github.black0nion.blackonionbot.utils;

public interface ThrowableConsumer<T, E extends Throwable> {
	void accept(T value) throws E; // NOSONAR dude that's literally the point of the throwable consumer lol
}
