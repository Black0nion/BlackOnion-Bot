package com.github.black0nion.blackonionbot.utils;

public interface ThrowableSupplier<T> {
	T get() throws Throwable;
}