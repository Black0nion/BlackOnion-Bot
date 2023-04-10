package com.github.black0nion.blackonionbot.utils;

public interface ThrowableFunction<T, R, E extends Exception> {
	R apply(T t) throws E;
}