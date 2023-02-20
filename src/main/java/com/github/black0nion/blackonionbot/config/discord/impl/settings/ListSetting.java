package com.github.black0nion.blackonionbot.config.discord.impl.settings;

import com.github.black0nion.blackonionbot.config.discord.api.settings.Setting;

import java.util.Collection;

public interface ListSetting<T, L extends Collection<T>> extends Setting<L> {
	default boolean add(T value) {
		boolean result = getValue().add(value);
		if (!result) return false;

		setValue(getValue());

		return true;
	}

	default void removeAll(L removedRoles) {
		getValue().removeAll(removedRoles);
		setValue(getValue());
	}

	default boolean remove(T value) {
		boolean result = getValue().remove(value);
		if (!result) return false;

		setValue(getValue());

		return true;
	}

	default boolean contains(T value) {
		return getValue().contains(value);
	}

	default boolean containsAll(L values) {
		return getValue().containsAll(values);
	}

}
