package com.github.black0nion.blackonionbot.config.discord.api.settings;

import com.github.black0nion.blackonionbot.config.common.exception.ParseException;
import com.github.black0nion.blackonionbot.config.discord.api.validation.Validator;

import java.util.List;

public interface Setting<T> {

	String getName();

	Class<T> getType();

	T getValue();

	Object toDatabaseValue();

	void setValue(T value);
	void setValueBypassing(T value);

	/**
	 * @return the classes that can be parsed to the right setting type
	 */
	List<Class<?>> canParse();
	default boolean canParse(Class<?> clazz) {
		return canParse().contains(clazz);
	}
	void setParsedValue(Object value) throws ParseException;

	/**
	 * Sets the value without saving it to the database
	 */
	void setParsedValueBypassing(Object value) throws ParseException;

	Validator<T>[] getValidators();
}
