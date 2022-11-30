package com.github.black0nion.blackonionbot.misc;

import org.jetbrains.annotations.NotNull;

/**
 * Helper class that can be extended, allows the subclass to initialize a specific field <i>before</i>
 * the other fields in said subclass are being initialized.
 */
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
