package com.github.black0nion.blackonionbot.config.featureflags.impl;

public abstract class AbstractFeatureFlag<T> {

	private final T value;

	protected AbstractFeatureFlag(T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	public boolean isEnabled() {
		return value != null;
	}

	public boolean isDisabled() {
		return value == null;
	}

	@Override
	public String toString() {
		return value == null ? "disabled" : value.toString();
	}
}
