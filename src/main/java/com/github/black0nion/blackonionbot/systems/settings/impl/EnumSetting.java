package com.github.black0nion.blackonionbot.systems.settings.impl;

import com.github.black0nion.blackonionbot.systems.settings.ConsumerCancellable;
import com.github.black0nion.blackonionbot.systems.settings.Setting;
import net.dv8tion.jda.internal.utils.Checks;

import java.util.Objects;
import java.util.function.Consumer;

public class EnumSetting<T extends Enum<?>> extends Setting<T> {
	private final Class<T> enumClass;
	private final T[] values;
	public EnumSetting(Class<T> clazz, String name, String descriptionKey, T defaultValue, Consumer<T> onChanged, ConsumerCancellable<T> preChanged, boolean nullable) {
		super(name, descriptionKey, defaultValue, onChanged, preChanged, nullable);
		this.enumClass = clazz;
		this.values = clazz.getEnumConstants();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected T parse(Object value) throws IllegalArgumentException {
		System.out.println(value + " | " + value.getClass());
		if (value instanceof String str) {
			for (T val : values) {
				if (val.name().equalsIgnoreCase(str))
					return val;
			}
		} else if (Objects.equals(enumClass, value.getClass())) {
			return (T) value;
		}
		throw new IllegalArgumentException("Invalid value type: " + value.getClass());
	}

	public static class Builder<T extends Enum<?>> extends SettingBuilder<Builder<T>, T, EnumSetting<T>> {
		private Class<T> enumClass;
		public Builder() {}

		@Override
		public Builder<T> defaultValue(T defaultValue) {
			if (defaultValue != null && enumClass == null)
				//noinspection unchecked
				enumClass = (Class<T>) defaultValue.getClass();
			return super.defaultValue(defaultValue);
		}

		public Builder<T> enumClass(Class<T> enumClass) {
			this.enumClass = enumClass;
			return this;
		}

		@Override
		protected EnumSetting<T> buildImpl() {
			Checks.notNull(enumClass, "enumClass");
			return new EnumSetting<>(enumClass, name, descriptionKey, defaultValue, onChanged, preChanged, nullable);
		}
	}
}