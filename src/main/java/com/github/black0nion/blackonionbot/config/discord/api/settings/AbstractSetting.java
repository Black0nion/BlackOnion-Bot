package com.github.black0nion.blackonionbot.config.discord.api.settings;

import com.github.black0nion.blackonionbot.config.common.exception.ParseException;
import com.github.black0nion.blackonionbot.config.discord.api.validation.Validator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

public abstract class AbstractSetting<T> implements Setting<T> {

	private final String name;
	private T value;

	private final Class<T> type;
	private final boolean nullable;
	@Nullable
	private final Validator<T>[] validators;

	@SafeVarargs
	protected AbstractSetting(String name, T defaultValue, Class<T> type, boolean nullable, @Nullable Validator<T>... validators) {
		this.name = name;
		this.type = type;
		this.nullable = nullable;
		this.validators = validators;
		setValue(defaultValue);
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
	public Class<T> getType() {
		return type;
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
		if (value == null) {
			if (nullable) {
				this.value = null;
				return;
			}
			throw new IllegalArgumentException("Value is null");
		}
		validate(value);
		this.value = value;
	}

	/**
	 * Will only be called if the value is of a type that can be parsed according to {@link #canParse()}
	 */
	protected abstract T parse(@Nonnull Object value) throws Exception; // NOSONAR will get wrapped in a ParseException

	@Override
	public void setParsedValue(Object value) throws ParseException {
		if (value == null) {
			if (nullable) {
				this.value = null;
				return;
			}
			throw new IllegalArgumentException("Value is null");
		}

		try {
			T parsedValue = parse(value);
			setValue(parsedValue);
		} catch (ParseException ex) {
			throw ex;
		} catch (Exception e) {
			throw new ParseException(e);
		}

		throw new ParseException("No parse factory for setting " + name);
	}

	/**
	 * @throws IllegalArgumentException if the value is not valid (according to the validators)
	 */
	private void validate(@Nonnull T value) {
		if (this.validators != null) {
			for (Validator<T> validator : validators) {
				if (!validator.test(value)) {
					throw new IllegalArgumentException("Value " + value + " is not valid for setting " + name);
				}
			}
		}
	}

	@Override
	public String toString() {
		return "AbstractSetting{" +
			"name='" + name + '\'' +
			", value=" + getType().getSimpleName() + "[" + value + "]" +
			", nullable=" + nullable +
			", validators=" + Arrays.toString(validators) +
			'}';
	}
}
