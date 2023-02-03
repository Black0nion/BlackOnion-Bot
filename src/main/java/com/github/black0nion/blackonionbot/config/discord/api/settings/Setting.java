package com.github.black0nion.blackonionbot.config.discord.api.settings;

import com.github.black0nion.blackonionbot.config.common.exception.ParseException;
import com.github.black0nion.blackonionbot.config.discord.api.validation.Validator;

import java.util.List;

public interface Setting<T> {

	String getName();

	Class<T> getType();

	T getValue();

	void setValue(T value);

	/**
	 * @return the classes that can be parsed to the right setting type
	 */
	List<Class<?>> canParse();
	void setParsedValue(Object value) throws ParseException;

	Validator<T>[] getValidators();
}
