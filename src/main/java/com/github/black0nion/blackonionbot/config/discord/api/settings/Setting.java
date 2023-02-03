package com.github.black0nion.blackonionbot.config.discord.api.settings;

import com.github.black0nion.blackonionbot.config.discord.api.validation.Validator;

public interface Setting<T> {
	String getName();

	T getValue();

	void setValue(T value);
	void setParsedValue(Object value);

	Validator<T>[] getValidators();
}
