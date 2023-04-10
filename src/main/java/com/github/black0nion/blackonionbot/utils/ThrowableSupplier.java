package com.github.black0nion.blackonionbot.utils;

public interface ThrowableSupplier<T> {
	T get() throws Exception; // NOSONAR dude that's literally the point of the throwable supplier lol
}
