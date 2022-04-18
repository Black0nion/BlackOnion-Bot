package com.github.black0nion.blackonionbot.systems.settings.impl;

import com.github.black0nion.blackonionbot.systems.settings.ConsumerCancellable;
import com.github.black0nion.blackonionbot.systems.settings.Setting;

import java.util.function.Consumer;

public class IntegerSetting extends Setting<Integer> {

	private final Integer min;
	private final Integer max;
	public IntegerSetting(String name, String descriptionKey, Integer defaultValue, Integer min, Integer max, Consumer<Integer> onChanged, ConsumerCancellable<Integer> preChanged, boolean nullable) {
		super(name, descriptionKey, defaultValue, onChanged, preChanged, nullable);
		this.min = min;
		this.max = max;
	}

	@Override
	protected boolean isValidValue(Integer value) {
		return (value != null || nullable) && (min == null || value >= min) && (max == null || value <= max);
	}

	@Override
	protected Integer parse(Object value) throws IllegalArgumentException {
		if (value instanceof Integer integer) {
			return integer;
		} else if (value instanceof String string) {
			try {
				return Integer.parseInt(string);
			} catch (NumberFormatException ignored) {
				throw new IllegalArgumentException("Invalid integer value: " + string);
			}
		}
		throw new IllegalArgumentException("Invalid integer value: " + value);
	}

	public static class Builder extends SettingBuilder<Builder, Integer, IntegerSetting> {
		private int min = Integer.MIN_VALUE;
		private int max = Integer.MAX_VALUE;

		public Builder min(int min) {
			this.min = min;
			return this;
		}

		public Builder max(int max) {
			this.max = max;
			return this;
		}

		@Override
		public IntegerSetting buildImpl() {
			return new IntegerSetting(name, descriptionKey, defaultValue, min, max, onChanged, preChanged, nullable);
		}
	}
}