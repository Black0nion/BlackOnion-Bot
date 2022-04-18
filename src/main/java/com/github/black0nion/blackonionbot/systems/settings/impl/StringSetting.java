package com.github.black0nion.blackonionbot.systems.settings.impl;

import com.github.black0nion.blackonionbot.systems.settings.ConsumerCancellable;
import com.github.black0nion.blackonionbot.systems.settings.Setting;
import lombok.Getter;
import org.bson.Document;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class StringSetting extends Setting<String> {

	@Getter
	private final Predicate<String> validator;

	public StringSetting(String name, String descriptionKey, String defaultValue, Predicate<String> validator, Consumer<String> onChanged, ConsumerCancellable<String> preChanged, boolean nullable) {
		super(name, descriptionKey, defaultValue, onChanged, preChanged, nullable);
		this.validator = validator;
	}

	@Override
	protected String parse(Object value) throws IllegalArgumentException {
		return String.valueOf(value);
	}

	@Override
	protected boolean isValidValue(String value) {
		if (value == null) {
			return nullable;
		}
		return validator == null || validator.test(value);
	}

	@Override
	protected String loadImpl(Document doc, String key) {
		return doc.getString(key);
	}

	public static class Builder extends SettingBuilder<Builder, String, StringSetting> {

		private Predicate<String> validator;

		public Builder validator(Predicate<String> validator) {
			this.validator = validator;
			return this;
		}

		@Override
		protected StringSetting buildImpl() {
			return new StringSetting(name, descriptionKey, defaultValue, validator, onChanged, preChanged, nullable);
		}
	}
}