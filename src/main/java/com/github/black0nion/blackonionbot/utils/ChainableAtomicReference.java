package com.github.black0nion.blackonionbot.utils;

import java.util.concurrent.atomic.AtomicReference;

public class ChainableAtomicReference<T> extends AtomicReference<T> {
	public ChainableAtomicReference(T initialValue) {
		super(initialValue);
	}

	public ChainableAtomicReference() {
		super();
	}

	public T setAndGet(T newValue) {
		super.set(newValue);
		return newValue;
	}
}
