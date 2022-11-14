package com.github.black0nion.blackonionbot.inject;

public interface Injector {
	<T> T createInstance(Class<?> toInstantiate, Class<T> expectedType);

	<T> T getInstance(Class<T> wantedClass);
}
