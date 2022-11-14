package com.github.black0nion.blackonionbot.misc;

import org.jetbrains.annotations.NotNull;

public class Holder<T> {

	@NotNull
	private final T value;

	public Holder(@NotNull T value) {
		this.value = value;
	}

	@NotNull
	public T getValue() {
		return value;
	}
}
