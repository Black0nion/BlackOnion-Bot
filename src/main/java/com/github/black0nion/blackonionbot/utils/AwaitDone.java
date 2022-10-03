package com.github.black0nion.blackonionbot.utils;

import java.util.function.Consumer;

public class AwaitDone<T> {
	private Consumer<T> onDone;

	public AwaitDone() {
		this(null);
	}

	public AwaitDone(Consumer<T> onDone) {
		this.onDone = onDone;
	}

	public void setOnDone(Consumer<T> onDone) {
		this.onDone = onDone;
	}

	public void done(T result) {
		if (this.onDone != null)
			this.onDone.accept(result);
	}
}
