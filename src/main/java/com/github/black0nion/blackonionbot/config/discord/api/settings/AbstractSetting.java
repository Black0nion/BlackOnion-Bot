package com.github.black0nion.blackonionbot.config.discord.api.settings;

import com.github.black0nion.blackonionbot.config.common.exception.ParseException;
import com.github.black0nion.blackonionbot.config.common.parse.ParseFactory;
import com.github.black0nion.blackonionbot.config.discord.api.validation.Validator;

import javax.annotation.Nullable;

public abstract class AbstractSetting<T> implements Setting<T> {

	private final String name;
	@Nullable
	private final Validator<T>[] validators;
	private T value;
	private final ParseFactory<T> parseFactory;

	@SafeVarargs
	protected AbstractSetting(String name, T value, @Nullable ParseFactory<T> parseFactory, @Nullable Validator<T>... validators) {
		this.name = name;
		this.validators = validators;
		this.parseFactory = parseFactory;
		setValue(value);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public T getValue() {
		return value;
	}

	@Override
	public Validator<T>[] getValidators() {
		return validators;
	}

	/**
	 * @throws IllegalArgumentException if the value is not valid (according to the validators)
	 */
	@Override
	public void setValue(T value) {
		validate(value);
		this.value = value;
	}

	@Override
	public void setParsedValue(String value) {
		if (parseFactory != null) {
			setValue(parseFactory.parse(value));
		}
		throw new ParseException("No parse factory for setting " + name);
	}

	/**
	 * @throws IllegalArgumentException if the value is not valid (according to the validators)
	 */
	private void validate(T value) {
		if (this.validators != null) {
			for (Validator<T> validator : validators) {
				if (!validator.test(value)) {
					throw new IllegalArgumentException("Value " + value + " is not valid for setting " + name);
				}
			}
		}
	}
}
