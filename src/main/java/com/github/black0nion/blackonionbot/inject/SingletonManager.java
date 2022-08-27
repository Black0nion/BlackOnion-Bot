package com.github.black0nion.blackonionbot.inject;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * This is an absolutely terrible way to do this, but unfortunately, guice is even worse than this, and other libraries like
 * avaje aren't meant for tests, which kind of eliminates the point of using DI in the first place.
 */
public class SingletonManager {
	/**
	 * Resolves Interface class to its implementation.
	 */
	protected static final Map<Class<?>, Object> singletonInstances = new HashMap<>();

	public static <T> SingletonBindingBuilder<T> bind(Class<T> interfaceClass) {
		return new SingletonBindingBuilder<>(interfaceClass);
	}

	public static class SingletonBindingBuilder<T> {
		private final Class<T> clazz;

		public SingletonBindingBuilder(Class<T> clazz) {
			this.clazz = clazz;
		}

		public void to(T instance) {
			SingletonManager.singletonInstances.put(this.clazz, instance);
		}
	}

	@Nonnull
	public static <T> T getSingleton(Class<T> clazz) {
		return clazz.cast(requireNonNull(singletonInstances.get(clazz), "Couldn't get singleton " + clazz.getName()));
	}
}
