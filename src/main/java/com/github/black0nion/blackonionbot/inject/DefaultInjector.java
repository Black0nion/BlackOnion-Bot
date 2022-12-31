package com.github.black0nion.blackonionbot.inject;

import com.github.black0nion.blackonionbot.config.immutable.api.Config;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import static java.util.Objects.requireNonNull;

public class DefaultInjector implements Injector {

	private final InjectorMap injectorMap;

	public DefaultInjector(Config config, InjectorMap injectorMap) {
		requireNonNull(config, "config");
		requireNonNull(injectorMap, "injectorMap");
		this.injectorMap = injectorMap;
		injectorMap.put(Injector.class, this);

		if (!injectorMap.containsKey(Config.class)) {
			injectorMap.put(Config.class, config);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T createInstance(Class<?> toInstantiate, Class<T> expectedType) {
		Constructor<?>[] constructors = toInstantiate.getConstructors();
		if (constructors.length != 1) {
			throw new IllegalArgumentException("Only one constructor allowed");
		}

		Constructor<?> constructor = constructors[0];
		Parameter[] parameters = constructor.getParameters();

		Object[] instances;
		try {
			instances = getInstances(Arrays.stream(parameters).map(Parameter::getType).toArray(Class<?>[]::new));
		} catch (IllegalArgumentException exception) {
			throw new InjectorCreateInstanceException("Could not create instance of " + toInstantiate.getName(), exception);
		}

		try {
			return (T) constructor.newInstance(instances);
		} catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
			throw new InjectorCreateInstanceException(e instanceof InstantiationException ? e.getCause() : e);
		}
	}

	@Override
	public Object[] getInstances(Class<?>... wantedClasses) {
		Object[] instances = new Object[wantedClasses.length];
		for (int i = 0; i < wantedClasses.length; i++) {
			Class<?> type = wantedClasses[i];
			if (injectorMap.containsKey(type)) {
				instances[i] = injectorMap.get(type);
			} else {
				throw new IllegalArgumentException("Entry for type '" + type.getName() + "' not found in map");
			}
		}
		return instances;
	}

	@Override
	public <T> T getInstance(Class<T> wantedClass) {
		return injectorMap.getInstance(wantedClass);
	}
}
