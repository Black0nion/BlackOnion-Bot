package com.github.black0nion.blackonionbot.inject;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("checkstyle:OverloadMethodsDeclarationOrder")
public class InjectorMap extends HashMap<Class<?>, Object> implements Map<Class<?>, Object> {

	public <T> T add(T instance) {
		requireNonNull(instance, "instance");
		put(instance);
		return instance;
	}

	public Object put(Object value) {
		requireNonNull(value, "value");
		Class<?>[] interfaces = value.getClass().getInterfaces();
		return put(interfaces.length == 1 ? interfaces[0] : value.getClass(), value);
	}

	public <T> T add(Class<?> key, T instance) {
		put(key, instance);
		return instance;
	}

	@Override
	public Object put(Class<?> key, Object value) {
		requireNonNull(key, "key");
		if (value == null || key.isInstance(value)) {
			return super.put(key, value);
		} else {
			throw new IllegalArgumentException("Value is not an instance of the key");
		}
	}
}
