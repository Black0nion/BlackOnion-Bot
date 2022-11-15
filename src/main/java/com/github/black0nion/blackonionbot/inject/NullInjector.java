package com.github.black0nion.blackonionbot.inject;

/**
 * The {@link #createInstance(Class, Class)} method is generic and thus {@code () -> null} can't be used as an {@link Injector}
 */
public class NullInjector implements Injector {
	public <T> T createInstance(Class<?> toInstantiate, Class<T> expectedType) {
		return null;
	}

	@Override
	public Object[] getInstances(Class<?>... wantedClasses) {
		return new Object[0];
	}

	@Override
	public <T> T getInstance(Class<T> wantedClass) {
		return null;
	}
}
