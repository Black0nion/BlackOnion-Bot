package com.github.black0nion.blackonionbot.inject;

import com.github.black0nion.blackonionbot.utils.SeparationClassNamer;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("checkstyle:OverloadMethodsDeclarationOrder")
public class InjectorMap extends HashMap<Class<?>, Object> implements Map<Class<?>, Object> {

	/**
	 * Puts the instance into the map.<br>
	 *<br>
	 * Algorithm used to get the class:<br>
	 * <pre>
	 * check interfaces
	 * 	if exactly one interface is implemented, use that
	 * 	else, use the class of the passed instance
	 * </pre>
	 */
	public <T> T add(T instance) {
		put(instance);
		return instance;
	}

	/**
	 * Puts the instance into the map.<br>
	 *<br>
	 * Algorithm used to get the class:<br>
	 * <pre>
	 * check interfaces
	 * 	if exactly one interface is implemented, use that
	 * 	else, use the class of the passed instance
	 * </pre>
	 */
	public Object put(Object instance) {
		requireNonNull(instance, "instance");
		return put(SeparationClassNamer.getInstanceClass(instance), instance);
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

	public <T> T getInstance(Class<T> wantedClass) {
		requireNonNull(wantedClass, "wantedClass");
		Object instance = get(wantedClass);
		if (instance == null) {
			throw new IllegalArgumentException("Entry for type '" + wantedClass.getName() + "' not found in map");
		}
		return wantedClass.cast(instance);
	}
}
